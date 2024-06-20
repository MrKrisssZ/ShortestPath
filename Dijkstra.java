import java.io.*;
import java.util.*;


public class Dijkstra implements Serializable
{
    // class for neighbours
    class Edge implements Serializable
    {
        String destination;
        int weight;

        Edge(String destination, int weight) 
        {
            this.destination = destination;
            this.weight = weight;
        }
    }
    class Node
    {
        String router;
        Node parent;
        int distance;
        Node (String router, Node parent, int distance)
        {
            this.router = router;
            this.parent = parent;
            this.distance = distance;
        }
        private void setParent (Node newParent)
        {
            this.parent = newParent;
        }
        private void setDistance (int newDistance)
        {
            this.distance = newDistance;
        }
    }
    public Map<String, List<Edge>> graph;
    // Constructor to initialize hash map
    public Dijkstra()
    {
        this.graph = new HashMap<>();
    }
    // Before 'LINKSTATE', we add each router into hash map as key and initialize the empty arraylist.
    public void addKey(String c)
    {
        graph.put(c, new ArrayList<>());
    }
    
    public void addEdge(String source, String destination, int weight)
    {
        // since it is an undirected graph, we need to add the edge information to hash map from both sides.
        Edge sourceEdge = new Edge(destination, weight);
        Edge destinationEdge = new Edge(source, weight);
        graph.get(source).add(sourceEdge);
        graph.get(destination).add(destinationEdge);
    }

    public void removeEdge(String source, String destination)
    {
        // since it is an undirected graph, we need to add the edge information to hash map from both sides.
        List<Edge> sourceEdges = graph.get(source);
        if (sourceEdges != null) 
        {
            sourceEdges.removeIf(edge -> edge.destination.equals(destination));
        }
        List<Edge> destinationEdges = graph.get(destination);
        if (destinationEdges != null) 
        {
            destinationEdges.removeIf(edge -> edge.destination.equals(source));
        }
    }

    public void updateEdge(String source, String destination, int weight)
    {
        // Update the existing edges
        List<Edge> sourceEdgeList = graph.get(source);
        List<Edge> destinationEdgeList = graph.get(destination);
        for (Edge edge : sourceEdgeList) 
        {
            if (edge.destination.equals(destination)) 
            {
                edge.weight = weight;
                break;
            }
        }
        for (Edge edge : destinationEdgeList)
        {
            if (edge.destination.equals(source))
            {
                edge.weight = weight;
                break;
            }
        }
    }

    public void updateNode(String source, String destination, int weight)
    {
        // If source node or destination node does not exist in the hash map, it means that it is a new router and we need to add it into hash map.
        // If two new routers are added into our graph
        if (!graph.containsKey(source) && !graph.containsKey(destination))
        {
            addKey(source);
            addKey(destination);
        }
        // if only one new router is added into our graph
        else if (!graph.containsKey(source))
        {
            addKey(source);
        }
        else if (!graph.containsKey(destination))
        {
            addKey(destination);
        }
    }
    // Check if node exists in current topology
    public boolean checkIfNodeExist(String router)
    {
        if (graph.containsKey(router))
        {
            return true;
        }
        return false;
    }
    // Check if edge exists
    public boolean checkIfEdgeExist(String source, String destination)
    {
        List<Edge> sourceEdgeList = graph.get(source);
        for (Edge e : sourceEdgeList)
        {
            if (e.destination.equals(destination))
            {
                return true;
            }
        }
        return false;
    }

    public void neighbourTable(String route)
    {
        System.out.println(route + " Neighbour Table:");
        List<Edge> neighbours = graph.get(route);

        // Sort the neighbours in order to follow the expected output in Myuni.
        Collections.sort(neighbours, (e1, e2) -> e1.destination.compareTo(e2.destination));

        for (Edge e : neighbours)
        {
            System.out.println(e.destination + "|" + e.weight);
        }
        System.out.println();
    }

    public List<String[]> lsDatabase(String route)
    { 
        // Create a hashmap for recording visited routes
        List<String> keyList = new ArrayList<>(graph.keySet());
        Map<String, Integer> visited = new HashMap<>();
        for (int i=0; i<keyList.size(); i++)
        {
            visited.put(keyList.get(i), i);
        }
        System.out.println(route + " LSDB:");
        List<String[]> sortedLSDB = new ArrayList<>();
        
        // Use Breadth First Search to compute lsdb
    
        bfs(route, visited, sortedLSDB);

        // Sort the LSDB alphabetically by using lambda comparator
        Collections.sort(sortedLSDB, (e1, e2) -> {
            if (e1[0].compareTo(e2[0]) == 0)
            {
                return e1[1].compareTo(e2[1]);
            }
            return e1[0].compareTo(e2[0]);
        });

        // Finally, print it out.
        for (String[] s : sortedLSDB)
        {
            System.out.println(s[0] + "|" + s[1] + "|" + s[2]);
        }
        System.out.println();
        return sortedLSDB;
    }

    public void bfs(String key, Map<String, Integer> visited, List<String[]> sortedLSDB)
    {
        
        List<Edge> edge = graph.get(key);
        visited.put(key, -1);
        // print it out first
        for (Edge e : edge)
        {
            String[] pair = new String[3];
            // If it is visited, then skip
            if (visited.get(e.destination) == -1)
            {
                continue;
            }

            // Compare horizontally, which means sort the source and detination alphabetically.
            if (key.compareTo(e.destination) < 0)
            {
                pair[0] = key;
                pair[1] = e.destination;
            }
            else
            {
                pair[0] = e.destination;
                pair[1] = key;
            }
            pair[2] = String.valueOf(e.weight);
            sortedLSDB.add(pair);
        }
        // Then do bfs for each neighbour
        for (Edge e : edge)
        {
            // If it is visited, then skip
            if (visited.get(e.destination) == -1)
            {
                continue;
            }
            bfs(e.destination, visited, sortedLSDB);
        }
    }

    public void routingTable (String router, Map<String, List<Edge>> graph, List<String[]> lsDatabase)
    {
        List<String[]> result = new ArrayList<>();
        System.out.println(router + " Routing Table:");
        /*
            Dijkstra with O(n^2):
            Initialization:
            2 N = {A}
            3 for all nodes v
            4 if v adjacent to A
            5 then D(v) = c(A,v)
            6 else D(v) = infinity
            7
            8 Loop
            9 find w not in N such that D(w) is a minimum
            10 add w to N
            11 update D(v) for all v adjacent to w and not in N:
            12 D(v) = min( D(v), D(w) + c(w,v) )
            "new cost to v is either old cost to v or known
            shortest path cost to w plus cost from w to v"
            12 until all nodes in N
        */

        /* Create an array list for storing and updating the information of each node and initialize the distance of each reachable node to infinity.
        Reachable nodes are obtained from the input route's LSDB.*/
        
        // Create a set for filering the unique reachable nodes from lsdb.
        Set<String> reachableNodes = new HashSet<>();
        for (String[] s : lsDatabase)
        {
            reachableNodes.add(s[0]);
            reachableNodes.add(s[1]);
        }
        // Isolated/unconnected router, then we just do nothing and return it.
        if (reachableNodes.size() == 0)
        {
            System.out.println();
            return;
        }

        // initialize the distance of each reachable node to infinity
        List<Node> nodeList = new ArrayList<>();
        for (String reachables : reachableNodes)
        {
            // we set the distance of start router to 0 because we poll the start router first to compare the adjacency router.
            if (reachables.equals(router))
            {
                nodeList.add(new Node(reachables, null, 0));
            }
            else
            {
                nodeList.add(new Node(reachables, null, Integer.MAX_VALUE));
            }
        }

        // Then start the Dijkstra algorithm stated in lecture, I create a list for the known shortetst path nodes and add the start route into it.
        List<Node> knownShortestPathOfNodes = new ArrayList<>();
        Node startRouter = findNodeByRouter(nodeList, router);
        knownShortestPathOfNodes.add(startRouter);
        nodeList.remove(startRouter);

        // For each adjacency of the start router, we change its distance from infinity to current distance.
        for (Edge adjacency : graph.get(router))
        {
            Node adjacentRouter = findNodeByRouter(nodeList, adjacency.destination);
            adjacentRouter.setDistance(adjacency.weight);
            adjacentRouter.setParent(startRouter);
        }
        
        // Loop part
        int total = reachableNodes.size();
        // until all nodes in N
        while (knownShortestPathOfNodes.size() != total)
        {   
            // string array for printing purpose
            String[] info = new String[3];

            // find w not in N such that D(w) is a minimum
            Node w = findMin(nodeList);

            // add w to N
            knownShortestPathOfNodes.add(w);
            List<Node> wShortest = generateShortestPath(w);
            info[0] = w.router;
            info[1] = wShortest.get(1).router;
            info[2] = String.valueOf(w.distance);
            result.add(info);

            // update D(v) for all v adjacent to w and not in N: D(v) = min( D(v), D(w) + c(w,v) )
            for (Edge v : graph.get(w.router))
            {
                Node adjacentRouter = findNodeByRouter(nodeList, v.destination);
                if (adjacentRouter == null)
                {
                    continue;
                }

                int c = v.weight;
                if (!knownShortestPathOfNodes.contains(adjacentRouter))
                {
                    // if there are two path with the same cost, then select the path with the least hops.
                    if (adjacentRouter.distance == w.distance + c)
                    {
                        List<Node> vShortest = generateShortestPath(adjacentRouter);
                        int nv = numberOfHops(vShortest);
                        int nw = numberOfHops(wShortest) + 1;
                        if (nv > nw)
                        {
                            adjacentRouter.setDistance(w.distance + c);
                            adjacentRouter.setParent(w);
                        }
                        // If the total number of hops of two paths are same,  which means multiple best routes exist and we select the first one in an alphabetical ordering of the next hop router name.*/
                        else if (nv == nw)
                        {
                            Node vNextHop = vShortest.get(1);
                            Node wNextHop = wShortest.get(1);
                            if (vNextHop.router.compareTo(wNextHop.router) > 0)
                            {
                                adjacentRouter.setDistance(w.distance + c);
                                adjacentRouter.setParent(w);
                            }
                        }
                    }
                    else
                    {
                        if (adjacentRouter.distance > w.distance + c)
                        {
                            adjacentRouter.setDistance(w.distance + c);
                            adjacentRouter.setParent(w);
                        }
                        else
                        {
                            // We do nothing and remain the same.
                        }
                    }
                }
            }
            // after each iteration, remove w from nodelist
            nodeList.remove(w);
        }
        // Print it out
        printOut(result);
    }
    public Node findNodeByRouter(List<Node> nodeList, String router) 
    {
        for (Node node : nodeList) 
        {
            if (node.router.equals(router)) 
            {
                return node;
            }
        }
        return null; // If no matching node is found
    }
    // Find the node with minimum routing distance in nodelist
    public Node findMin(List<Node> list)
    {
        int min = Integer.MAX_VALUE;
        Node result = null;
        for (Node n : list)
        {
            if (n.parent == null)
            {
                continue;
            }
            if (n.distance < min)
            {
                min = n.distance;
                result = n;
            }
            // if multiple least cost paths exist to select from in an iteration, always select the first one in alphabetical order
            else if (n.distance == min)
            {
                // If the distance is infinity, then we directly skip it.
                if (result == null)
                {
                    continue;
                }
                if (result.router.compareTo(n.router) > 0)
                {
                    result = n;
                }
            }
        }
        return result;
    }
    // Method for generating the whole shorest path with start node
    public List<Node> generateShortestPath(Node source)
    {
        List<Node> path = new ArrayList<>();
        // Trace back by the property 'parent' in Node variable type
        Node iterator = source;
        while (iterator != null)
        {
            path.add(iterator);
            iterator = iterator.parent;
        }
        // Reverse it
        Collections.reverse(path);
        return path;
    }
    // Method for calculating the number of hops based on the generated shortest path.
    public int numberOfHops(List<Node> path)
    {
        int total = 0;
        for (int i=0; i<path.size(); i++)
        {
            total++;
        }
        return total;
    }
    // Print out final routing table
    public void printOut(List<String[]> result)
    {
        // Sort router in shorstest path alphabetically for the requirement of routing table.
        Collections.sort(result, (e1, e2) -> e1[0].compareTo(e2[0]));
        for (String[] s : result)
        {
            System.out.println(s[0] + "|" + s[1] + "|" + s[2]);
        }
        System.out.println();
    }

    public static void main(String[] args)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Dijkstra rt = new Dijkstra();

        try 
        {
            String line;
            while (!(line = reader.readLine()).equals("LINKSTATE")) {
                // Just read the router names and add them into map as key.
                // We assume all routers will appear in the edges part
                rt.addKey(line);
            }

            // Read edges until "UPDATE" keyword
            while (!(line = reader.readLine()).equals("UPDATE")) 
            {
                // Use split() method to split the parts of string by space
                String[] parts = line.split(" ");
                // Use split() method again to seperate the source node and destination node by '-'
                String[] nodes = parts[0].split("-");
                String source = nodes[0];
                String destination = nodes[1];
                int weight = Integer.parseInt(parts[1]);

                // based on the provided link state, we need to build our graph by the following add/remove method.
                if (weight == -1) 
                {
                    rt.removeEdge(source, destination);
                } 
                else 
                {
                    if (rt.checkIfEdgeExist(source, destination))
                    {
                        rt.updateEdge(source, destination, weight);
                    }
                    else
                    {
                        rt.addEdge(source, destination, weight);
                    }
                }
                
                // print the neighbour table, lsdb and routing table when there are three entries in the parts array.
                if (parts.length > 2)
                {
                    String[] routes = parts[2].split(",");
                    for (String route : routes)
                    {
                        rt.neighbourTable(route);
                        List<String[]> lsdb = rt.lsDatabase(route);
                        rt.routingTable(route, rt.graph, lsdb);
                    }
                }
            }

            // Read edges until "END" keyword
            while (!(line = reader.readLine()).equals("END")) 
            {
                // Use split() method to split the parts of string by space
                String[] parts = line.split(" ");
                // Use split() method again to seperate the source node and destination node by '-'
                String[] nodes = parts[0].split("-");
                String source = nodes[0];
                String destination = nodes[1];
                int weight = Integer.parseInt(parts[1]);

                // based on the provided updating link state, we need to update our graph.
                if (weight == -1) 
                {
                    // only new routers found but no link between them, which means just single/two router is found.
                    if (!rt.checkIfNodeExist(source) || !rt.checkIfNodeExist(destination))
                    {
                        rt.updateNode(source, destination, weight);
                    }
                    else
                    {  
                        rt.removeEdge(source, destination);
                    }
                } 
                else 
                {
                    // If there are one or two unseen routers. Then we add them into our graph and update the edge.
                    if (!rt.checkIfNodeExist(source) || !rt.checkIfNodeExist(destination))
                    {
                        rt.updateNode(source, destination, weight);
                        rt.addEdge(source, destination, weight);
                    }
                    else
                    {
                        if (rt.checkIfEdgeExist(source, destination))
                        {
                            rt.updateEdge(source, destination, weight);
                        }
                        else
                        {
                            rt.addEdge(source, destination, weight);
                        }
                    }
                }

                // print the neighbour table, lsdb and routing table when there are three entries in the parts array.
                if (parts.length > 2)
                {
                    String[] routes = parts[2].split(",");
                    for (String route : routes)
                    {
                        rt.neighbourTable(route);
                        List<String[]> lsdb = rt.lsDatabase(route);
                        rt.routingTable(route, rt.graph, lsdb);
                    }
                }
            }
            // After running the Dijkstra algorithm, save the graph to a file by outputStream
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("graph_dijkstra.ser"))) 
            {
                oos.writeObject(rt.graph);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        } 
        catch (Exception e) 
        {
            System.out.println(e);
        }

    }
}
