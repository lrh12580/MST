package none.example.com.mst;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by admin on 2018/5/13.
 */
public class CustomView extends View {

    private ArrayList<PointF> vertices = new ArrayList<>();
    private ArrayList<Edge> edges = new ArrayList<>();
    private ArrayList<Integer> hideNodes = new ArrayList<>();
    private Paint mPaint = new Paint();
    private int procedure = 0;

    private int left_index = 0;
    private int right_index = 0;
    public static final int RADIUS = 50;
    private Status currentStatus = Status.idle;
    private int weight = 0;
    private boolean createdGraph = false;
    private Context mContext;

    public CustomView(Context context) {
        super(context);
        Log.d("CustomView", "HaHa1");
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        Log.d("CustomView", "HaHa2");

    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.d("CustomView", "HaHa3");
    }

    public void addPoint() {
        vertices.add(new PointF(100, 100));
        postInvalidate();
    }

    public void update(DirectedGraph directedGraph) {
        edges = new ArrayList<>();
        hideNodes = new ArrayList<>();
        Vertex[] vertices = directedGraph.getVertices();
        for (int i = 0; i < vertices.length; i++)
            if (!vertices[i].isVisible())
                hideNodes.add(i);
        int[][] weights = directedGraph.getWeights();
        for (int i = 0; i < directedGraph.getVertex_number(); i++) {
            for (int j = 0; j < directedGraph.getVertex_number(); j++) {
                if (weights[i][j] != DirectedGraph.noEdge) {
                    Edge edge = new Edge(i, j, weights[i][j]);
                    edges.add(edge);
                }
            }
        }
        postInvalidate();
    }

    public void clear() {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        procedure = 0;
        left_index = 0;
        right_index = 0;
        weight = 0;
        mPaint = new Paint();
        createdGraph = false;
        currentStatus = Status.idle;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //参数为圆的横坐标 ,纵坐标,半径,创建

        for (int i = 0; i < vertices.size(); i++) {
            if (hideNodes.indexOf(i) == -1) {
                if (left_index == i)
                    mPaint.setColor(Color.GRAY);
                else
                    mPaint.setColor(Color.BLACK);
                canvas.drawCircle(vertices.get(i).x, vertices.get(i).y, RADIUS, mPaint);
                mPaint.setTextSize(RADIUS);
                mPaint.setColor(Color.WHITE);
                canvas.drawText(i + "", vertices.get(i).x - RADIUS / 2 + 5, vertices.get(i).y + RADIUS / 2 - 5, mPaint);
            }
        }

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(5.0f);
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).getWeight() != DirectedGraph.noEdge) {
                float startX = vertices.get(edges.get(i).getLeftVertexIndex()).x;
                float startY = vertices.get(edges.get(i).getLeftVertexIndex()).y;
                float endX = vertices.get(edges.get(i).getRightVertexIndex()).x;
                float endY = vertices.get(edges.get(i).getRightVertexIndex()).y;
                float slope = (startY - endY) / (endX - startX);
                double angle = Math.atan(slope);
                Log.d("CustomView", "slope: " + slope);
                Log.d("CustomView", "angle: " + angle);
                if (startX < endX) {
                    startX += Math.cos(angle) * RADIUS;
                    startY -= Math.sin(angle) * RADIUS;
                    endX -= Math.cos(angle) * RADIUS;
                    endY += Math.sin(angle) * RADIUS;
                } else {
                    startX -= Math.cos(angle) * RADIUS;
                    startY += Math.sin(angle) * RADIUS;
                    endX += Math.cos(angle) * RADIUS;
                    endY -= Math.sin(angle) * RADIUS;
                }

                float x1 = endX;
                float x2 = endX;
                float y1 = endY;
                float y2 = endY;
                float x = endX;
                float y = endY;
                float lamda = 15.0f;

                float slope_ = -1.0f / slope;
                double angle_ = Math.atan(slope_);

                if (startX < endX) {
                    x = (float) (endX - Math.cos(angle) * lamda * 2);
                    y = (float) (endY + Math.sin(angle) * lamda * 2);
                } else {
                    x = (float) (endX + Math.cos(angle) * lamda * 2);
                    y = (float) (endY - Math.sin(angle) * lamda * 2);
                }

                x1 = (float) (x + Math.cos(angle_) * lamda);
                y1 = (float) (y - Math.sin(angle_) * lamda);
                x2 = (float) (x - Math.cos(angle_) * lamda);
                y2 = (float) (y + Math.sin(angle_) * lamda);

                canvas.drawLine(startX, startY, endX, endY, mPaint);
                canvas.drawLine(x1, y1, endX, endY, mPaint);
                canvas.drawLine(x2, y2, endX, endY, mPaint);
                mPaint.setTextSize(60f);
                canvas.drawText(edges.get(i).getWeight() + "", startX + (endX - startX) / 3,
                        startY + (endY - startY) / 3, mPaint);
            }

        }
    }

    //触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获得触摸事件
        if ((currentStatus == Status.idle || currentStatus == Status.addVertex || currentStatus == Status.createGraph) && vertices.size() > 0) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (int i = 0; i < vertices.size(); i++) {
                        if (Math.pow((vertices.get(i).x - event.getX()), 2) + Math.pow((vertices.get(i).y - event.getY()), 2) <= Math.pow(RADIUS, 2)) {
                            left_index = i;
                            Log.d("CustomView", "left_index: " + left_index);
                        }
                    }
                    break;
                //ACTION_MOVE不要设置break，否则圆形不会跟随手指活动 只会手指松开屏幕的时候圆形直接到了屏幕停止的位置
                case MotionEvent.ACTION_MOVE:
                case MotionEvent.ACTION_UP:
                    //获取手指触摸位置的x坐标
                    vertices.get(left_index).x = event.getX();
                    //获取手指触摸位置的y坐标
                    vertices.get(left_index).y = event.getY();
                    //启动
                    postInvalidate();
                    currentStatus = Status.idle;
                    break;
            }
        } else if (currentStatus == Status.addEdge && vertices.size() > 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    for (int i = 0; i < vertices.size(); i++) {
                        if (Math.pow((vertices.get(i).x - event.getX()), 2) + Math.pow((vertices.get(i).y - event.getY()), 2) <= Math.pow(RADIUS, 2)) {
                            left_index = i;
                            Log.d("CustomView", "left_index: " + left_index);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    for (int i = 0; i < vertices.size(); i++) {
                        if (Math.pow((vertices.get(i).x - event.getX()), 2) + Math.pow((vertices.get(i).y - event.getY()), 2) <= Math.pow(RADIUS, 2)) {
                            right_index = i;
                            Log.d("CustomView", "right_index: " + right_index);
                            if (right_index != left_index) {

                                LayoutInflater li = LayoutInflater.from(this.getContext());
                                View promptsView = li.inflate(R.layout.prompts, null);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                        this.getContext());

                                // set prompts.xml to alertdialog builder
                                alertDialogBuilder.setView(promptsView);

                                final EditText userInput = (EditText) promptsView
                                        .findViewById(R.id.edgeWeight);

                                // set dialog message
                                alertDialogBuilder
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        // get user input and set it to result
                                                        // edit text
                                                        try {
                                                            weight = Integer.parseInt(userInput.getText().toString());
                                                            Edge edge = new Edge(left_index, right_index, weight);
                                                            edges.add(edge);
                                                            currentStatus = Status.idle;
                                                            postInvalidate();
                                                            Log.d("CustomView", "weight: " + userInput.getText());
                                                        }catch (Exception e) {
                                                            Toast.makeText(mContext, "Please input an integer", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                // create alert dialog
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                // show it
                                alertDialog.show();
                            }
                        }
                    }
                    break;
            }
        }
        return true;
    }

    public Status getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Status currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ArrayList<PointF> getVertices() {
        return vertices;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public boolean isCreatedGraph() {
        return createdGraph;
    }

    public void setCreatedGraph(boolean createdGraph) {
        this.createdGraph = createdGraph;
    }

    public int getProcedure() {
        return procedure;
    }

    public void setProcedure(int procedure) {
        this.procedure = procedure;
    }

    public void incProcedure() {
        this.procedure++;
    }

    public void decProcedure() {
        this.procedure--;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
