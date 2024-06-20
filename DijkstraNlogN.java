import java.io.*;
import java.util.*;

public class DijkstraNlogN implements Serializable
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
    public DijkstraNlogN()
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
            Dijkstra with O(nlogn):
            1 Initialization:
            2 For all nodes 
            3 Initialize their distance and parent to infinity and null respectively except for start router, we set the distance of it to zero.
            4 Initialize a hashmap called visited for recording the visited nodes.
            5 Initialize PriorityQueue = {}
            6 PriorityQueue.insert(A)
            -------------------------
            7 Loop
            8 Retrieves and removes the head of this priority queue, which is w and it means D(w) is a minimum because the priority rule of this queue is the distance
            e.g. whose distance short, then enjoys the priority (poll out first). 
            If it is visited, then continue
            9 update D(v) for all v adjacent to w:
                if (v is visited): then continue
                if D(w) + c(w,v) < D(v)
                    D(v) = D(w) + c(w,v)
                    parent[v] = w
                    Insert v into the pq (Even if v is
                    already there)
                    ( We don't do the operation below because priority queue in java does not have function decreaseKey.
                    PriorityQueue in java does not automatically re-order its elements if the properties of an already enqueued element change.
                    so we have to re-add the node to the priority queue to ensure the queue maintains the correct order whenever its distance is updated.
                    previous operation: if (v is in priority queue). then PQ.decreaseKey(v) else  PQ.insert(v) )
                else we do nothing
            10 until priority queue is empty.
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

        // Initialize the distance and parent of each reachable node to infinity and null respectively
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

        // Initialize a hashmap called visited for recording the visited nodes.
        Map<Node, Integer> visited = new HashMap<>();
        for (Node n : nodeList)
        {
            visited.putIfAbsent(n, 0);
        }

        // Initialize Priority Queue with the self-defined priority rule: multiple least cost paths exist to select from in an iteration, always select the first one in alphabetical order
        // Otherwise, we select the one with smaller distance.
        PriorityQueue<Node> pq = new PriorityQueue<>((v1, v2) -> {
            if (v1.distance == v2.distance)
            {
                return v1.router.compareTo(v2.router);
            }
            return v1.distance - v2.distance;
        });

        // Insert the start router into Priority Queue.
        Node startRouter = findNodeByRouter(nodeList, router);
        pq.add(startRouter);

        
        // Loop part
        // until priority queue is empty
        while (!pq.isEmpty())
        {   
            // string array for printing purpose
            String[] info = new String[3];

            // Retrieves and removes the head of this priority queue, which has minimum distance.
            Node w = pq.poll();

            // We have to check it first because it may contains the visited node based on our algorithm
            if (visited.get(w) == 1)
            {
                continue;
            }
            
            // visited the Node w
            visited.put(w, 1);

            List<Node> wShortest = generateShortestPath(w);

            // If the polled router is the start router, we don't add information into the result.
            if (wShortest.size() != 1)
            {
                info[0] = w.router;
                // next hop
                info[1] = wShortest.get(1).router;
                info[2] = String.valueOf(w.distance);
                result.add(info);
            }

            // update D(v) for all v adjacent to w: D(v) = min( D(v), D(w) + c(w,v) )
            for (Edge v : graph.get(w.router))
            {
                Node adjacentRouter = findNodeByRouter(nodeList, v.destination);
                // if no adjacentRouter or it is visited, then continue looping.
                if (adjacentRouter == null || visited.get(adjacentRouter) == 1)
                {
                    continue;
                }

                int c = v.weight;
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
                        pq.add(adjacentRouter);
                    }
                    // if it is not in the pq, then we add it. It's a bit different from previous Dijkstra version.
                    else if (nv < nw)
                    {
                        // We do nothing
                    }
                    // If the total number of hops of two paths are same, which means multiple best routes exist and we select the first one in an alphabetical ordering of the next hop router name.
                    else if (nv == nw)
                    {
                        Node vNextHop = vShortest.get(1);
                        Node wNextHop = wShortest.get(1);
                        if (vNextHop.router.compareTo(wNextHop.router) > 0)
                        {
                            adjacentRouter.setDistance(w.distance + c);
                            adjacentRouter.setParent(w);
                            pq.add(adjacentRouter);
                        }
                        else
                        {
                            // we do nothing
                        }
                    }
                }
                else
                {
                    if (adjacentRouter.distance > w.distance + c)
                    {
                        // if (v is in priority queue). then PQ.decreaseKey(v); else  PQ.insert(v)
                        adjacentRouter.setDistance(w.distance + c);
                        adjacentRouter.setParent(w);
                        pq.add(adjacentRouter);
                    }
                    else
                    {
                        // We do nothing and remain the same.
                    }
                }
            }
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
        DijkstraNlogN nlogn = new DijkstraNlogN();

        try 
        {
            String line;
            while (!(line = reader.readLine()).equals("LINKSTATE")) {
                // Just read the router names and add them into map as key.
                // We assume all routers will appear in the edges part
                nlogn.addKey(line);
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
                    nlogn.removeEdge(source, destination);
                } 
                else 
                {
                    if (nlogn.checkIfEdgeExist(source, destination))
                    {
                        nlogn.updateEdge(source, destination, weight);
                    }
                    else
                    {
                        nlogn.addEdge(source, destination, weight);
                    }
                }
                
                // print the neighbour table, lsdb and routing table when there are three entries in the parts array.
                if (parts.length > 2)
                {
                    String[] routes = parts[2].split(",");
                    for (String route : routes)
                    {
                        nlogn.neighbourTable(route);
                        List<String[]> lsdb = nlogn.lsDatabase(route);
                        nlogn.routingTable(route, nlogn.graph, lsdb);
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
                    if (!nlogn.checkIfNodeExist(source) || !nlogn.checkIfNodeExist(destination))
                    {
                        nlogn.updateNode(source, destination, weight);
                    }
                    else
                    {  
                        nlogn.removeEdge(source, destination);
                    }
                } 
                else 
                {
                    // If there are one or two unseen routers. Then we add them into our graph and update the edge.
                    if (!nlogn.checkIfNodeExist(source) || !nlogn.checkIfNodeExist(destination))
                    {
                        nlogn.updateNode(source, destination, weight);
                        nlogn.addEdge(source, destination, weight);
                    }
                    else
                    {
                        if (nlogn.checkIfEdgeExist(source, destination))
                        {
                            nlogn.updateEdge(source, destination, weight);
                        }
                        else
                        {
                            nlogn.addEdge(source, destination, weight);
                        }
                    }
                }

                // print the neighbour table, lsdb and routing table when there are three entries in the parts array.
                if (parts.length > 2)
                {
                    String[] routes = parts[2].split(",");
                    for (String route : routes)
                    {
                        nlogn.neighbourTable(route);
                        List<String[]> lsdb = nlogn.lsDatabase(route);
                        nlogn.routingTable(route, nlogn.graph, lsdb);
                    }
                }
            }

            // After running the Dijkstra algorithm, save the graph to a file by outputStream
            try (ObjectOutputStream oosNlogN = new ObjectOutputStream(new FileOutputStream("graph_dijkstra_nlogn.ser"))) 
            {
                oosNlogN.writeObject(nlogn.graph);
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