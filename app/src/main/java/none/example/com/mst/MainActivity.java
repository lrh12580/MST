package none.example.com.mst;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CustomView mCustomView;
    private Button mAddVertexButton;
    private Button mAddEdgeButton;
    private Button mCreateGraphButton;
    private Button mNextButton;
    private Button mPreviousButton;
    private Button mClearButton;
    private ArrayList<DirectedGraph> graphs;
    private boolean isConnected = false;
    private int root = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mCustomView = (CustomView) findViewById(R.id.customView);
        mCustomView.setmContext(MainActivity.this);
        mAddVertexButton = (Button) findViewById(R.id.addVertexButton);
        mAddEdgeButton = (Button) findViewById(R.id.addEdgeButton);
        mCreateGraphButton = (Button) findViewById(R.id.createGraph);
        mNextButton = (Button) findViewById(R.id.next);
        mPreviousButton = (Button) findViewById(R.id.previous);
        mClearButton = (Button) findViewById(R.id.clear);

        mAddVertexButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomView.addPoint();
                mCustomView.setCurrentStatus(Status.addVertex);
            }
        });

        mAddEdgeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomView.setCurrentStatus(Status.addEdge);
            }
        });

        mCreateGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                View setRootView = li.inflate(R.layout.set_root, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(setRootView);

                final EditText userInput = (EditText) setRootView
                        .findViewById(R.id.root_index);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        try {
                                            root = Integer.parseInt(userInput.getText().toString());
                                        } catch (Exception e) {
                                            Toast.makeText(MainActivity.this, "Please input an integer", Toast.LENGTH_SHORT).show();
                                        }
                                            if (root < mCustomView.getVertices().size()) {
                                                mCustomView.setCurrentStatus(Status.createGraph);
                                                mCustomView.setCreatedGraph(true);
                                                DirectedGraph initialGraph = new DirectedGraph(mCustomView.getVertices(), mCustomView.getEdges());
                                                mCustomView.update(initialGraph);
                                                if (!initialGraph.MST(root)) {
                                                    Toast.makeText(MainActivity.this, "This graph is not connected", Toast.LENGTH_SHORT).show();
                                                    isConnected = false;
                                                } else {
                                                    isConnected = true;
                                                    graphs = initialGraph.getGraphs();
                                                    Log.d("MainActivity", "graph.size(): " + graphs.size());
                                                }
                                            } else {
                                                Toast.makeText(MainActivity.this, "This vertex does not exist", Toast.LENGTH_SHORT).show();
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
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCustomView.isCreatedGraph() && isConnected) {
                    mCustomView.update(graphs.get(mCustomView.getProcedure() + 1));
                    if (mCustomView.getProcedure() < graphs.size() - 2)
                        mCustomView.incProcedure();
                }
            }
        });

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCustomView.isCreatedGraph() && isConnected) {
                    if (mCustomView.isCreatedGraph()) {
                        mCustomView.update(graphs.get(mCustomView.getProcedure()));
                        if (mCustomView.getProcedure() > 0)
                            mCustomView.decProcedure();
                    }
                }
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCustomView.clear();
            }
        });
    }

}
