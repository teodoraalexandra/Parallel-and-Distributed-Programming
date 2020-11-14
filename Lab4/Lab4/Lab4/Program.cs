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
            Callbacks.Run(hosts);
        }
    }
}