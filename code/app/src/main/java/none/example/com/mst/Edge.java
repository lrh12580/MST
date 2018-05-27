package none.example.com.mst;

/**
 * Created by admin on 2018/5/14.
 */

public class Edge {
    private int leftVertexIndex;
    private int rightVertexIndex;
    private int weight;

    public Edge(int leftVertexIndex, int rightVertexIndex) {
        this.leftVertexIndex = leftVertexIndex;
        this.rightVertexIndex = rightVertexIndex;
    }

    public Edge(int leftVertexIndex, int rightVertexIndex, int weight) {
        this.leftVertexIndex = leftVertexIndex;
        this.rightVertexIndex = rightVertexIndex;
        this.weight = weight;
    }

    public int getLeftVertexIndex() {
        return leftVertexIndex;
    }

    public void setLeftVertexIndex(int leftVertexIndex) {
        this.leftVertexIndex = leftVertexIndex;
    }

    public int getRightVertexIndex() {
        return rightVertexIndex;
    }

    public void setRightVertexIndex(int rightVertexIndex) {
        this.rightVertexIndex = rightVertexIndex;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
