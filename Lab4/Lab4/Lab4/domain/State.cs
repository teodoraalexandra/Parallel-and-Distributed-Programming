using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;

namespace Lab4.domain
{
    public class State
    {
        public Socket Socket = null;
        public const int BufferSize = 56;
        public readonly byte[] Buffer = new byte[BufferSize];
        public readonly StringBuilder ResponseContent = new StringBuilder();

        public int Id; 
        public string Hostname;
        public string Endpoint;
        public IPEndPoint RemoteEndpoint;

        public readonly ManualResetEvent ConnectMutex = new ManualResetEvent(false);
        public readonly ManualResetEvent SendMutex = new ManualResetEvent(false);
        public readonly ManualResetEvent ReceiveMutex = new ManualResetEvent(false);
    }
}