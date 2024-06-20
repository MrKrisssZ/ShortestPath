#!/bin/bash

# Compile Java programs
javac Dijkstra.java
javac DijkstraNlogN.java
javac TopologyExtractor.java

# Test cases
test_cases=("test1.txt" "test2.txt" "test3.txt" "test4.txt" "test5.txt" "test6.txt")

printf "%-20s %-20s %-25s %-25s\n" "Number of Nodes" "Number of Links" "Execution Time (N*N) (s)" "Execution Time (N log N) (s)"

# Function to run Dijkstra. The { ...; } 2>&1 captures both stdout and stderr. As by default, it only captures stderr, then pipes the output to grep real to find the line with the real time, and finally uses awk '{print $2}' to extract the time value.
run_dijkstra() 
{
    output=$(java Dijkstra < $1)
    exec_time=$( { time java Dijkstra < $1; } 2>&1 | grep real | awk '{print $2}')
    echo "$output"
    echo "$exec_time"
}

# Function to run DijkstraNlogN
run_dijkstra_nlogn() 
{
    output=$(java DijkstraNlogN < $1)
    exec_time=$( { time java DijkstraNlogN < $1; } 2>&1 | grep real | awk '{print $2}')
    echo "$output"
    echo "$exec_time"
}

# Loop through test cases
for test_case in "${test_cases[@]}"
do
    total_time_n2=0
    total_time_nlogn=0

    # measure execution ten times
    for ((i=1; i<=10; i++))
    do
        output_n2=$(run_dijkstra $test_case)
        time_n2=$(echo "$output_n2" | tail -n 1 | tr -d 'ms')
        total_time_n2=$(echo "$total_time_n2 + $time_n2" | bc)
        
        output_nlogn=$(run_dijkstra_nlogn $test_case)
        time_nlogn=$(echo "$output_nlogn" | tail -n 1 | tr -d 'ms')
        total_time_nlogn=$(echo "$total_time_nlogn + $time_nlogn" | bc)
    done

    # Take the average time and results will be rounded to three decimal places
    avg_time_n2=$(echo "scale=3; $total_time_n2 / 10" | bc)
    avg_time_nlogn=$(echo "scale=3; $total_time_nlogn / 10" | bc)

    java TopologyExtractor graph_dijkstra.ser > topology_info.txt
    java TopologyExtractor graph_dijkstra_nlogn.ser > topology_info_nlogn.txt
    nodes=$(grep "Final number of nodes: " topology_info.txt | awk '{print $5}')
    links=$(grep "Final number of edges: " topology_info.txt | awk '{print $5}')

    # Format and print the results
    printf "%-20s %-20s %-25s %-25s\n" "$nodes" "$links" "$avg_time_n2" "$avg_time_nlogn"

    # Capture output
    output_n2=$(run_dijkstra $test_case)
    output_nlogn=$(run_dijkstra_nlogn $test_case)

    output_n2=$(echo "$output_n2" | sed '$d')
    output_nlogn=$(echo "$output_nlogn" | sed '$d')

    # Write the results to individual files
    test_case_name=$(basename $test_case .txt)
    echo "$output_n2" > "Dijkstra${test_case_name}.txt"
    echo "$output_nlogn" > "DijkstraNlogN${test_case_name}.txt"
    
done

echo "Results for individual test cases saved to separate files"

# Clean up temporary files
rm -f topology_info.txt topology_info_nlogn.txt graph_dijkstra.ser graph_dijkstra_nlogn.ser *.class

