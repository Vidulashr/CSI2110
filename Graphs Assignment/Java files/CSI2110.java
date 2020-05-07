//NAME: Vidulash Rajaratnam
//NUMBER: 8190398

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csi2110_project;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.print.attribute.standard.NumberOfDocuments;


/**
 *
 * @author Administrator
 */
public class CSI2110 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String edgesFilename = "email-dnc.edges";
        Graph                       graph = readGraph(edgesFilename);
        List<Integer>               nodes = graph.getGraphNodes();
        Map<Integer, List<Integer>> edges = graph.getGraphEdges();

        //Answer is below
        System.out.println("------------------------------------------------------------------------------------------------------------");
        ArrayList<Pair<Integer,Double>> initialPR = makeList(graph,1); //set initial PR values at 1
        ArrayList<Pair<Integer,Double>> updatedPR = makeList(graph,1); //set initial PR values at 1 for main list
        //Printing the initial PR values for all the nodes
        System.out.print("Initial PR values: ");
        System.out.println(initialPR);

        //For loop below to update the values in the main list
        long beforetime = System.nanoTime();
        for (int i = 0;i<10;i++){ //iterates 10 times for more precise value
            PRiteration(initialPR,graph,0,nodes.size(),updatedPR); //update PR values for all nodes
            initialPR = updatedPR; //make base list equal to updated list to loop again
        }
        long aftertime = System.nanoTime();
        System.out.println("Compute time: "+((aftertime-beforetime)/1000000000)); //prints computation time in seconds
        //Printing the final PR values for all nodes after 50 iterations of updates
        System.out.print("Final PR values: ");
        System.out.println(updatedPR);

        //Printing the list of the top 10 influential nodes
        System.out.println("Here are the 10 most influential nodes: ");
        int count = 1;
        for (Pair<Integer,Double> i:getTopTen(updatedPR)){
            System.out.print(count+++". ");
            System.out.println(i);
        } //shows top ten most influential nodes
        System.out.println("------------------------------------------------------------------------------------------------------------");
        //Answer is above^



        System.out.println("Number of nodes in the Graph: " + nodes.size());
        for(Integer node : nodes) {
            System.out.println("Node number: " + node);
            System.out.print("Adjacent Nodes: ");
            if (edges.containsKey(node)) {
                for(Integer edge : edges.get(node)) {
                    System.out.print(edge + " ");
                }
            }
            System.out.println();
            System.out.println("------------------------------------");
        }
    }
    
    private static Graph readGraph(String edgesFilename) throws FileNotFoundException, IOException {
        System.getProperty("user.dir");
        URL edgesPath = csi2110_project.CSI2110.class.getResource(edgesFilename);
        BufferedReader csvReader = new BufferedReader(new FileReader(edgesPath.getFile()));
        String row;
        List<Integer>               nodes = new ArrayList<Integer>();
        Map<Integer, List<Integer>> edges = new HashMap<Integer, List<Integer>>(); 
        
        boolean first = false;
        while ((row = csvReader.readLine()) != null) {
            if (!first) {
                first = true;
                continue;
            }
            
            String[] data = row.split(",");
            
            Integer u = Integer.parseInt(data[0]);
            Integer v = Integer.parseInt(data[1]);
            
            if (!nodes.contains(u)) {
                nodes.add(u);
            }
            if (!nodes.contains(v)) {
                nodes.add(v);
            }
            
            if (!edges.containsKey(u)) {
                // Create a new list of adjacent nodes for the new node u
                List<Integer> l = new ArrayList<Integer>();
                l.add(v);
                edges.put(u, l);
            } else {
                edges.get(u).add(v);
            }
        }
        
        for (Integer node : nodes) {
            if (!edges.containsKey(node)) {
                edges.put(node, new ArrayList<Integer>());
            }
        }
        
        csvReader.close();
        return new Graph(nodes, edges);
    }


    //METHODS ADDED BELOW:

    //Method that returns a list of nodes that outgoing from the target node A
    private static ArrayList<Integer> getOutgoingLinks(int A,Graph g){
        //List of all the nodes for Graph g
        List<Integer> n = g.getGraphNodes();
        //List of all edges for Graph g
        Map<Integer, List<Integer>> e = g.getGraphEdges();
        //List of all edges for node A
        ArrayList<Integer> eforA = new ArrayList<Integer>();
        //Iterates through all the nodes for Graph g
        for (Integer node:n){
            if (node==A){ //if the node is equal to A
                if (e.containsKey(node)) {
                    //Iterates through all edges in Graph g
                    for(Integer edge : e.get(node)) { //for all edges associated to node A
                        //if (!eforA.contains(edge)){ //CHECK
                        eforA.add(edge);} //adds all edges for node A in ArrayList eforA
                    }
                }
            }
        //}
        return eforA;//return array list of nodes outgoing from target node A
    }

    //Method that gets all nodes Ti that direct toward the target node A
    private static ArrayList<Integer> getTiToA(int A,Graph g){
        //List of all the nodes for Graph g
        List<Integer>               n = g.getGraphNodes();
        //List of all edges for Graph g
        Map<Integer, List<Integer>> e = g.getGraphEdges();
        //List of all edges for node A
        ArrayList<Integer> eforA = new ArrayList<Integer>();
        for (Integer node:n){ //in all the available nodes in the graph g
            for (Integer no:e.get(node)){ //for all the edges that are part of the target node A
                if (no.equals(A)){ //if the edge is equal to
                   // if (!eforA.contains(node)){//CHECK if its already in the list
                        eforA.add(node);} //if its not, add it to list
                }
            }
       // }
        return eforA; //return the list of integers directed towards A
    }

    //Makes a list of all nodes with a initial PR value of value
    private static ArrayList<Pair<Integer,Double>> makeList(Graph g,double value){
        List<Integer> n = g.getGraphNodes();
        ArrayList<Pair<Integer,Double>> PRvalues = new ArrayList<Pair<Integer,Double>>(); //initiates arraylist
        for(Integer nodes:n){ //for all nodes in the graph
            Pair<Integer,Double> temp = new Pair<Integer, Double>(nodes, value); //create a pair of node and a PR value of value
            PRvalues.add(temp); //add pair to arraylist
        }
        return PRvalues; //return arraylist of node,PR value
    }

    //Method that updates the PR value of a node at index count in a array of pairs
    private static ArrayList<Pair<Integer,Double>> getPRforNode(ArrayList<Pair<Integer,Double>> a, Graph g,int count) {
        double PR = 0; //initiate initial PR value as 0 to be added later
        final double d = 0.85; //damping factor

        Integer key = a.get(count).getKey(); //get the integer value of the node at index count in the list
        ArrayList<Integer> edges = getTiToA(key,g); //get the list of nodes that direct towards the key node

        if (edges.isEmpty()){ //if list is empty
            PR = (1-d); //PR values is calculated
            a.set(count,new Pair<Integer, Double>(key,PR)); //PR value is updated
            return a; //return the new updated list of pairs
        }

        for (Integer e:edges){ //for all nodes that direct towards key node
            int ind = retrieve(e,a); //get index of e in list if pairs with PR values
            ArrayList<Integer> eEdge = getOutgoingLinks(e,g); //get the amount of outgoing links
            Double value = a.get(ind).getValue(); //get current PR value of e
            if (eEdge.size()>1){ //if it has more than 1 outgoing link
                PR += value/eEdge.size();}
            else{
                PR+= value; }
        }
        Double newPR = (1-d)+(d*PR); //calculate new PR value
        a.set(count,new Pair<Integer, Double>(key,newPR)); //update the new PR value in list
        return a; //returns list with updated pair for node at index count
    }

    //Method that iterates through list of pairs<node, PR value> and updates there PR value
    private static ArrayList<Pair<Integer,Double>> PRiteration(ArrayList<Pair<Integer,Double>> cons, Graph g,int count, int size,ArrayList<Pair<Integer,Double>> upd){
        if (count==size){ //if iterated through size of nodes, returns array
            return upd; //if it went through all the nodes, doesnt update PR values anymore, returns list
        }
        else{ //if it hasnt gone through all values in list of pairs
            upd = getPRforNode(upd,g,count); //creates new array with updated PR values
            count++; //increases count
            return PRiteration(cons,g,count,size,upd); //recursive and keeps repeating till count = size
        }
    }

    //Method that returns the index of the Pair that contains the target key node from a array of pairs
    public static int retrieve(Integer key,ArrayList<Pair<Integer,Double>> a) {
        // iterating on each element of the list would suffice to check if a key exists in the list
        for (int i = 0;i<a.size();i++) { // iterating on list of 'Pair's
            if (a.get(i).getKey().equals(key)) {  // if key is equal to target key
                return i; //return the index
            }
        }
        return -1; //if key not found, returns -1
    }

    //Method that gives an ArrayList of Pairs of the most influential nodes in a graph
    public static ArrayList<Pair<Integer,Double>> getTopTen(ArrayList<Pair<Integer,Double>> a){
        if (a.size()<=10){ //if arraylist size is 10 or less
            return a; //returns same arraylist
        }
        //else, if bigger than 10 in size
        ArrayList<Pair<Integer,Double>> topTenfinal = new ArrayList<Pair<Integer,Double>>(); //initiates new arraylist of pairs
        Pair<Integer,Double> topTen=null; //initiates temporary pair of node and PR value
        for (int n = 0;n<10;n++) { //loops till new arraylist is exactly 10 in size
            for (int i = 0; i < a.size(); i++) { //loops through all nodes in a
                if (topTen==null) { //if first node, just make the temporary pair
                    topTen = new Pair<Integer, Double>(a.get(i).getKey(),a.get(i).getValue());
                } else { //for every other node
                    if (a.get(i).getValue() > topTen.getValue()) { //if PR valyes of node is greater than the one in the temporary pair
                        topTen = new Pair<Integer, Double>(a.get(i).getKey(), a.get(i).getValue()); //change pair to this
                    }
                }
            }
            topTenfinal.add(topTen); //after the top node is found add to top 10 arraylist
            a.remove(retrieve(topTen.getKey(),a)); //remove that list from original arraylist so it doesnt repeat
            topTen = null; //clear temporary pair
        }
        return topTenfinal; //return pairs with highest PR value
    }


    //METHODS ADDED ABOVE:
}
