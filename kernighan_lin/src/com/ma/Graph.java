package com.ma;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by simplay on 15/02/16.
 */
public class Graph {

    public final List<Vertex> vertices = new ArrayList<Vertex>();

    public Graph() {

    }

    public void appendVertex(Vertex v) {
        vertices.add(v);
    }

    public void updateSimilaritiesForVertex(int vertex_id, String[] similarities) {
        Vertex v = vertices.get(vertex_id);
        v.setSimilarities(similarities);
    }

    public void inspectSimilaritiesForVertex(int id) {
        for(Float t : vertices.get(0).similarities) {
            System.out.print(t + " ");
        }
    }
}
