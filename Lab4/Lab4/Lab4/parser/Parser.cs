using System;

namespace Lab4.parser
{
    public static class Parser
    {
        public const int HttpPort = 80;
        
        public static string GetRequestString(string hostname, string endpoint)
        {
            var result = "GET " + endpoint + " HTTP/1.1\r\n" +
                         "Host: " + hostname + "\r\n" +
                         "User-Agent: PostmanRuntime/7.26.5\r\n" +
                         "Accept: */*\r\n" +
                         "Accept-Charset: ASCII\r\n" +
                         "Accept-Encoding: gzip, deflate, br\r\n" +
                         "Connection: keep-alive\r\n\r\n";
            return result;
        }
        
        public static string GetResponseHeader(string responseContent) {
            var responseParts = responseContent.Split(new[] {"\r\n\r\n"}, StringSplitOptions.RemoveEmptyEntries);
            return responseParts.Length > 1 ? responseParts[0] : "";
        }
        
        public static string GetResponseBody(string responseContent) {
            var responseParts = responseContent.Split(new[] {"\r\n\r\n"}, StringSplitOptions.RemoveEmptyEntries);
            return responseParts.Length > 1 ? responseParts[1] : "";
        }

        public static bool ReceiveFullHeader(string responseContent) {
            return responseContent.Contains("\r\n\r\n");
        }
        
        public static int GetContentLength(string responseContent) {
            var contentLength = 0;
            var responseLines = responseContent.Split('\r', '\n');

            foreach (var responseLine in responseLines) {
                var headerDetails = responseLine.Split(':');

                // Parse headers and get value from Content-Length
                // Ex. for ~rlupsa should be 1462
                if (string.Compare(headerDetails[0], "Content-Length", StringComparison.Ordinal) == 0) {
                    contentLength = int.Parse(headerDetails[1]);
                }
            }

            return contentLength;
        }
    }
}