package com.ma;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by simplay on 16/02/16.
 */
public class Partition implements Iterable<Vertex> {

    private final List<Vertex> vertices = new ArrayList<>();

    public Partition() {
    }

    @Override
    public Iterator<Vertex> iterator() {
        return vertices.iterator();
    }

}
