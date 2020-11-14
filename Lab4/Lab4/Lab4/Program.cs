using System;
using System.Collections.Generic;
using Lab4.tpl;

namespace Lab4
{
    internal static class Program
    {
        private static readonly List<string> hosts = new List<string> {
            "www.cs.ubbcluj.ro/~rlupsa/edu/pdp/",
            "www.buzias.ro/phoenix.html", 
            "www.google.com/",
        };

        static void Main(string[] args)
        {
            // Directly implement the parser on the callbacks (event-driven)
            Console.WriteLine("Start callbacks ...");
            //Callbacks.Run(hosts);
            Console.WriteLine("\n\n");
            
            // Wrap the connect/send/receive operations in tasks,
            // with the callback setting the result of the task
            Console.WriteLine("Start tasks ...");
            TaskMechanism.Run(hosts);
            Console.WriteLine("\n\n");
            
            // Like the previous, but also use the async/await mechanism
            Console.WriteLine("Start async/await tasks ...");
            //Async.Run(hosts);
            Console.WriteLine("\n\n");
        }
    }
}