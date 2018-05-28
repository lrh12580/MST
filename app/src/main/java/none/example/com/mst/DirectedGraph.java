package none.example.com.mst;

import android.graphics.Color;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by admin on 2018/5/14.
 */

public class DirectedGraph {

    public static final int noEdge = Integer.MAX_VALUE;

    private int[][] weights;
    private Vertex[] vertices;
    private int vertex_number = 0;
    private int edges = 0;
    private int time = 0;
    private ArrayList<Integer> topological_sort = new ArrayList<>();
    private ArrayList<DirectedGraph> graphs = new ArrayList<>();
    private ArrayList<Integer> hideNodes = new ArrayList<>();

    public DirectedGraph(ArrayList<PointF> vertices, ArrayList<Edge> edges) {
        this.vertex_number = vertices.size();
        this.edges = edges.size();

        weights = new int[this.vertex_number][this.vertex_number];

        for (int i = 0; i < weights.length; i++)
            for (int j = 0; j < weights[i].length; j++)
                weights[i][j] = noEdge;

        for (int i = 0; i < this.edges; i++) {
            int left_index = edges.get(i).getLeftVertexIndex();
            int right_index = edges.get(i).getRightVertexIndex();

            weights[left_index][right_index] = edges.get(i).getWeight();
        }

        this.vertices = new Vertex[vertex_number];
        for (int i = 0; i < vertex_number; i++)
            this.vertices[i] = new Vertex();
    }

    public DirectedGraph(int[][] weights) {
        this.weights = weights;

        this.vertex_number = weights.length;
        this.edges = 0;
        for (int i = 0; i < vertex_number; i++)
            for (int j = 0; j < vertex_number; j++)
                if (weights[i][j] != noEdge)
                    this.edges++;

        this.vertices = new Vertex[vertex_number];
        for (int i = 0; i < vertex_number; i++)
            this.vertices[i] = new Vertex();
    }


    public DirectedGraph fStarToMST(int r, DirectedGraph fStar) {
        int vertex_number = fStar.getVertex_number();
        int fStar_weights[][] = fStar.getWeights();
        int weights[][] = new int[vertex_number][vertex_number];

        for (int i = 0; i < vertex_number; i++) {
            for (int j = 0; j < vertex_number; j++) {
                if (fStar_weights[i][j] != noEdge)
                    weights[i][j] = this.weights[i][j];
                else
                    weights[i][j] = noEdge;
            }
        }

        DirectedGraph directedGraph = new DirectedGraph(weights);
        directedGraph.setHideNodes(fStar.getHideNodes());
        directedGraph.setVertices(fStar.getVertices());
        return directedGraph;
    }

    public boolean MST(int r) {
        ArrayList<DirectedGraph> directedGraphs = new ArrayList<>();
        MST(r, this, directedGraphs);
        //if(!MST(r, this, directedGraphs)) {
        //   graphs.clear();
        //    return false;
        //}
        DirectedGraph finalGraph = graphs.get(graphs.size() - 1);
        Log.d("DirectedGraph", "directedGraphs_size: " + directedGraphs.size());
        for (int i = directedGraphs.size() - 2; i >= 0; i--) {
            DirectedGraph directedGraph = directedGraphs.get(i);
            finalGraph = directedGraph.removeCircle(r, directedGraph, finalGraph);
            graphs.add(finalGraph);
        }

        //graphs.add(finalGraph);
        return true;
    }

    public boolean MST(int r, DirectedGraph graph, ArrayList<DirectedGraph> return_graphs) {
        return_graphs.add(graph);
        graphs.add(graph);
        DirectedGraph modified = graph.modifyCosts(r, graph);
        graphs.add(modified);
        DirectedGraph fStar = graph.FStar(r, modified);
        graphs.add(fStar);
        ArrayList<Integer> circles = graph.isMST(r, fStar);
        DirectedGraph finalGraph;
        if (circles != null) {
            finalGraph = graph.contract(circles, r, graph, modified);

            MST(r, finalGraph, return_graphs);
        } else {
            finalGraph = graph.fStarToMST(r, fStar);
            graphs.add(finalGraph);
        }
        return true;
    }

    public DirectedGraph modifyCosts(int r, DirectedGraph graph) {
        int[][] weights = new int[graph.getVertex_number()][graph.getVertex_number()];
        for (int i = 0; i < graph.getVertex_number(); i++)
            for (int j = 0; j < graph.getVertex_number(); j++)
                weights[i][j] = graph.getWeights()[i][j];

        Log.d("DirectedGraph", "vertex_number: " + graph.getVertex_number());
        for (int j = 0; j < graph.getVertex_number(); j++) {
            if (j != r) {
                int min = noEdge;

                for (int i = 0; i < graph.getVertex_number(); i++)
                    if (graph.getWeights()[i][j] != noEdge && graph.getWeights()[i][j] < min)
                        min = graph.getWeights()[i][j];

                for (int i = 0; i < graph.getVertex_number(); i++)
                    if (weights[i][j] != noEdge)
                        weights[i][j] -= min;
            } else {
                for (int i = 0; i < graph.getVertex_number(); i++)
                    weights[i][j] = noEdge;
            }
        }

        DirectedGraph directedGraph = new DirectedGraph(weights);
        directedGraph.setHideNodes(graph.getHideNodes());
        directedGraph.setVertices(graph.getVertices());
        return directedGraph;
    }

    public DirectedGraph FStar(int r, DirectedGraph graph) {
        int[][] weights = new int[graph.getVertex_number()][graph.getVertex_number()];

        for (int i = 0; i < graph.getVertex_number(); i++)
            for (int j = 0; j < graph.getVertex_number(); j++)
                weights[i][j] = noEdge;

        first:
        for (int j = 0; j < graph.getVertex_number(); j++) {
            if (j != r) {
                for (int i = 0; i < graph.getVertex_number(); i++) {
                    if (graph.getWeights()[i][j] == 0) {
                        weights[i][j] = 0;
                        continue first;
                    }
                }
            }
        }

        DirectedGraph directedGraph = new DirectedGraph(weights);
        directedGraph.setHideNodes(graph.getHideNodes());
        directedGraph.setVertices(graph.getVertices());
//        int edges = directedGraph.getEdges();
//        if (edges != directedGraph.getVertex_number() - 1)
//            return null;
        return directedGraph;
    }

    public DirectedGraph contract(ArrayList<Integer> circle, int r, DirectedGraph graph, DirectedGraph modified) {
        Log.d("DirectedGraph", "circle: " + circle);

        int vertex_number = graph.getVertex_number();
        int[][] graph_weights = graph.getWeights();
        int[][] modified_weights = modified.getWeights();
        int[][] weights = new int[vertex_number][vertex_number];
        for (int i = 0; i < vertex_number; i++)
            for (int j = 0; j < vertex_number; j++)
                weights[i][j] = noEdge;
        int superNode = circle.get(0);

        for (int i = 0; i < vertex_number; i++) {
            int[] temp_weights = new int[circle.size()];
            for (int m = 0; m < temp_weights.length; m++)
                temp_weights[m] = modified_weights[i][circle.get(m)];
            int min_index = 0;
            for (int m = 0; m < temp_weights.length; m++)
                if (temp_weights[m] < temp_weights[min_index])
                    min_index = m;
            min_index = circle.get(min_index);
            for (int j = 0; j < vertex_number; j++) {
                if (graph_weights[i][j] != noEdge) {
                    if (circle.indexOf(i) == -1 && circle.indexOf(j) == -1)
                        weights[i][j] = graph_weights[i][j];
                    else if (circle.indexOf(i) == -1 && circle.indexOf(j) != -1) {
                        weights[i][superNode] = graph_weights[i][min_index];
                    } else if (circle.indexOf(i) != -1 && circle.indexOf(j) == -1) {
                        if (weights[superNode][j] > graph_weights[i][j]) {
                            weights[superNode][j] = graph_weights[i][j];
                        }

                    }
                }
            }
        }

        DirectedGraph directedGraph = new DirectedGraph(weights);
        ArrayList<Integer> node = new ArrayList<>();
        Vertex vertices[] = new Vertex[graph.getVertex_number()];
        for (int i = 0; i < vertices.length; i++)
            vertices[i] = new Vertex();
        for (int i = 0; i < circle.size(); i++) {
            node.add(circle.get(i));
            if (i != 0)
                vertices[circle.get(i)].setVisible(false);
        }

        Vertex[] vertices_ = graph.getVertices();
        for (int i = 1; i < vertices_.length; i++)
            if (!vertices_[i].isVisible())
                vertices[i].setVisible(false);
        directedGraph.setHideNodes(node);
        directedGraph.setVertices(vertices);
        return directedGraph;
    }

    public DirectedGraph removeCircle(int r, DirectedGraph graph, DirectedGraph fStar) {
        ArrayList<Integer> circle = fStar.getHideNodes();
        Log.d("DirectedGraph", "circle: " + circle);
        int superNode = circle.get(0);
        int graph_weights[][] = graph.getWeights();
        int fStar_weights[][] = fStar.getWeights();

        int vertex_number = graph.getVertex_number();
        int[][] weights = new int[vertex_number][vertex_number];
        for (int i = 0; i < vertex_number; i++)
            for (int j = 0; j < vertex_number; j++)
                weights[i][j] = noEdge;

        int index = superNode;
        for (int i = 0; i < vertex_number; i++) {
            first:
            for (int j = 0; j < vertex_number; j++) {
                if (i == superNode || j == superNode && fStar_weights[i][j] != noEdge) {
                    if (i == superNode && j != superNode) {
                        for (int m = 0; m < circle.size(); m++) {
                            if (graph_weights[circle.get(m)][j] == fStar_weights[i][j]) {
                                weights[circle.get(m)][j] = fStar_weights[i][j];
                                Log.d("DirectedGraph", "weights[circle.get(m)][j]:"+weights[circle.get(m)][j]);
                                continue first;
                            }
                        }
                    } else if (i != superNode && j == superNode) {
                        for (int m = 0; m < circle.size(); m++) {
                            if (graph_weights[i][circle.get(m)] == fStar_weights[i][j]) {
                                weights[i][circle.get(m)] = fStar_weights[i][j];
                                index = circle.get(m);
                                continue first;
                            }
                        }
                    }
                } else if (i != superNode && j != superNode && fStar_weights[i][j] != noEdge) {
                    weights[i][j] = fStar_weights[i][j];
                }
            }
        }

        for (int i = 0; i < circle.size(); i++) {

            int right_index = circle.get(i % (circle.size()));
            int left_index = circle.get((i + 1) % (circle.size()));
            Log.d("DirectedGraph", "left_index: " + left_index);
            Log.d("DirectedGraph", "right_index: " + right_index);
            Log.d("DirectedGraph", "weights: " + graph.getWeights()[left_index][right_index]);
            if (right_index != index)
                weights[left_index][right_index] = graph.getWeights()[left_index][right_index];
        }

        DirectedGraph directedGraph = new DirectedGraph(weights);
        directedGraph.setHideNodes(graph.getHideNodes());
        directedGraph.setVertices(graph.getVertices());
        return directedGraph;
    }

    public ArrayList<Integer> isMST(int r, DirectedGraph graph) {
        graph.dfs(r);
        ArrayList<Integer> topological_sort = graph.getTopological_sort();
        Log.d("DirectedGraph", "topological_sort:" + graph.getTopological_sort());
        for (int i = 0; i < topological_sort.size(); i++) {
            int index = topological_sort.get(i);
            for (int j = 0; j < graph.getVertex_number(); j++) {
                if (graph.getWeights()[index][j] != noEdge) {
                    ArrayList<Integer> circle = new ArrayList<>();
                    circle.add(index);
                    if (graph.getWeights()[j][index] != noEdge) {
                        circle.add(j);
                        return circle;
                    }
                    int pre = graph.getVertices()[index].getPredecessor();
                    while (pre != -1) {
                        circle.add(pre);
                        pre = graph.getVertices()[pre].getPredecessor();
                        if (pre == j) {
                            circle.add(j);
                            return circle;
                        }
                    }
                }
            }
        }
        return null;
    }

    public void dfs(int r) {
        topological_sort = new ArrayList<>();
        for (int i = 0; i < vertex_number; i++) {
            vertices[i].setColor(Color.WHITE);
            vertices[i].setPredecessor(-1);
        }
        time = 0;
        for (int i = 0; i < vertex_number; i++) {
            if (vertices[(i + r) % vertex_number].getColor() == Color.WHITE)
                this.dfsVisit((i + r) % vertex_number);
        }
    }

    public void dfsVisit(int u) {
        time++;
        vertices[u].setDiscoverTime(time);
        vertices[u].setColor(Color.GRAY);
        for (int i = 0; i < vertex_number; i++) {
            if (weights[u][i] != noEdge) {
                if (vertices[i].getColor() == Color.WHITE) {
                    vertices[i].setPredecessor(u);
                    this.dfsVisit(i);
                }
            }
        }
        vertices[u].setColor(Color.BLACK);
        time++;
        vertices[u].setFinishTime(time);
        topological_sort.add(0, u);
    }

    public int getVertex_number() {
        return vertex_number;
    }

    public int getEdges() {
        return edges;
    }

    public int[][] getWeights() {
        return weights;
    }

    public ArrayList<Integer> getTopological_sort() {
        return topological_sort;
    }

    public void setTopological_sort(ArrayList<Integer> topological_sort) {
        this.topological_sort = topological_sort;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public void setVertex(int index, Vertex vertex) {
        this.vertices[index] = vertex;
    }

    public ArrayList<DirectedGraph> getGraphs() {
        return graphs;
    }

    public void setGraphs(ArrayList<DirectedGraph> graphs) {
        this.graphs = graphs;
    }

    public ArrayList<Integer> getHideNodes() {
        return hideNodes;
    }

    public void setHideNodes(ArrayList<Integer> hideNodes) {
        this.hideNodes = hideNodes;
    }


}
