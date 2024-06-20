import java.io.*;
import java.util.*;

public class TopologyExtractor implements Serializable
{
    // Serialization is the process of converting an object's state into a byte stream, This is useful for saving an object's state to a file
    class Edge implements Serializable 
    {
        String destination;
        int weight;
        private static final long serialVersionUID = 1L;
        Edge(String destination, int weight) 
        {
            this.destination = destination;
            this.weight = weight;
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) 
    {
        // Check if there exists graph file.
        if (args.length != 1) 
        {
            System.err.println("Usage: java TopologyExtractor <graph_file>");
            System.exit(1);
        }

        String graphFile = args[0];
        Map<String, List<Edge>> graph = new HashMap<>();

        // read the file and store it into variable by inputStream
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(graphFile))) 
        {
            graph = (Map<String, List<Edge>>) ois.readObject();
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }

        Set<String> nodes = graph.keySet();
        // Because our graph is undirected, every adjacent node is calculated twice
        int numEdges = graph.values().stream().mapToInt(List::size).sum() / 2;

        System.out.println("Final number of nodes: " + nodes.size());
        System.out.println("Final number of edges: " + numEdges);
    }
}


