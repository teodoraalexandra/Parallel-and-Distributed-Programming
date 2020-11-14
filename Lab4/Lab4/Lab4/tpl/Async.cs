using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using Lab4.domain;
using Lab4.parser;

namespace Lab4.tpl
{
    public static class Async
    {
        private static List<string> hosts;
        
        public static void Run(List<string> hostnames) {
            hosts = hostnames; 

            var tasks = new List<Task>();

            // Wrap the connect/send/receive operations in tasks,
            // with the callback setting the result of the task
            for (var i = 0; i < hosts.Count; i++) {
                tasks.Add(Task.Factory.StartNew(DoStart, i));
            }

            Task.WaitAll(tasks.ToArray());
        }
         
        private static void DoStart(object idObject) {
            var id = (int) idObject;

            StartClient(hosts[id], id);
        }
        
        private static async void StartClient(string host, int id) {
            // ReSharper disable once MethodHasAsyncOverload
            var ipHostInfo = Dns.GetHostEntry(host.Split('/')[0]);
            var ipAddress = ipHostInfo.AddressList[0];
            var remoteEndpoint = new IPEndPoint(ipAddress, Parser.HttpPort);
            
            var client = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

            var state = new State {
                Socket = client,
                Hostname = host.Split('/')[0],
                Endpoint = host.Contains("/") ? host.Substring(host.IndexOf("/", StringComparison.Ordinal)) : "",
                RemoteEndpoint = remoteEndpoint,
                Id = id
            };

            // Connect to the remote endpoint  
            await ConnectWrapper(state);

            // Request 
            await SendWrapper(state);

            // Response
            await ReceiveWrapper(state);
            
            client.Shutdown(SocketShutdown.Both);
            client.Close();
        }
        
        private static async Task ConnectWrapper(State state) {
            state.Socket.BeginConnect(state.RemoteEndpoint, ConnectCallback, state);

            // Wait for the connection mutex to be set
            await Task.FromResult<object>(state.ConnectMutex.WaitOne());
        }
        
        private static void ConnectCallback(IAsyncResult ar) {
            var state = (State) ar.AsyncState;
            if (state == null) return;
            var clientSocket = state.Socket;
            var clientId = state.Id;
            var hostname = state.Hostname;
 
            clientSocket.EndConnect(ar);

            Console.WriteLine(clientId + " is connected to " + hostname + " " + clientSocket.RemoteEndPoint);

            // Signal that the connection is done 
            state.ConnectMutex.Set();
        }

        private static async Task SendWrapper(State state) {
            // First, we have to convert string to bytes 
            var byteData = Encoding.ASCII.GetBytes(Parser.GetRequestString(state.Hostname, state.Endpoint));
 
            state.Socket.BeginSend(byteData, 0, byteData.Length, 0, SendCallback, state);

            // Wait for the sent mutex to be set
            await Task.FromResult<object>(state.SendMutex.WaitOne());
        }
        
        private static void SendCallback(IAsyncResult ar) {
            var state = (State) ar.AsyncState;
            if (state == null) return;
            var clientSocket = state.Socket;
            var clientId = state.Id;
 
            var bytesSent = clientSocket.EndSend(ar);
            Console.WriteLine(clientId + " has sent " + bytesSent + " bytes to server.");

            // Signal that the sent is done 
            state.SendMutex.Set();
        }

        private static async Task ReceiveWrapper(State state) {
            state.Socket.BeginReceive(state.Buffer, 0, State.BufferSize, 0, ReceiveCallback, state);

            // Wait for the receive mutex to be set
            await Task.FromResult<object>(state.ReceiveMutex.WaitOne());
        }
        
        private static void ReceiveCallback(IAsyncResult ar) {
            var state = (State) ar.AsyncState;
            if (state == null) return;
            var clientSocket = state.Socket;
            var clientId = state.Id;

            try { 
                var bytesRead = clientSocket.EndReceive(ar);

                state.ResponseContent.Append(Encoding.ASCII.GetString(state.Buffer, 0, bytesRead));
                
                // If the response header has not been fully obtained, get the next chunk of data
                if (!Parser.ReceiveFullHeader(state.ResponseContent.ToString())) {
                    clientSocket.BeginReceive(state.Buffer, 0, State.BufferSize, 0, ReceiveCallback, state);
                } else {
                    // Header has been fully obtained, so we can get the body
                    var responseBody = Parser.GetResponseBody(state.ResponseContent.ToString());

                    // When we get the Content-Length, we know how much data we should receive
                    // If we got less data, the BeginReceive must be called again
                    var contentLengthHeaderValue = Parser.GetContentLength(state.ResponseContent.ToString());
                    if (responseBody.Length < contentLengthHeaderValue) {
                        clientSocket.BeginReceive(state.Buffer, 0, State.BufferSize, 0, ReceiveCallback, state);
                    } else {
                        Console.WriteLine(clientId + " received: " + responseBody.Length + " chars (body)");
                        Console.WriteLine(Parser.GetResponseHeader(state.ResponseContent.ToString()) + "\n\n");
                        
                        // Signal that the receive is done 
                        state.ReceiveMutex.Set();
                    }
                }
            } catch (Exception e) {
                Console.WriteLine(e.ToString());
            }
        }
    }
}