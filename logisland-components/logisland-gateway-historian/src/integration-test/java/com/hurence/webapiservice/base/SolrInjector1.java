package com.hurence.webapiservice.base;

import com.hurence.logisland.record.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolrInjector1 extends AbstractSolrInjector {

    @Override
    protected List<ChunkExpected> buildListOfChunks() {
        List<ChunkExpected> chunks = new ArrayList<>();
        chunks.add(buildChunk1());
        chunks.add(buildChunk2());
        chunks.add(buildChunk3());
        return chunks;
    }

    private ChunkExpected buildChunk1() {
        ChunkExpected chunk = new ChunkExpected();
        chunk.points = Arrays.asList(
                new Point(0, 1L, 5),
                new Point(0, 2L, 8),
                new Point(0, 3L, 1.2),
                new Point(0, 4L, 6.5)
        );
        chunk.compressedPoints = compressPoints(chunk.points);
        chunk.start = 1L;
        chunk.end = 4L;
        chunk.sum = chunk.points.stream().mapToDouble(Point::getValue).sum();
        chunk.avg = chunk.sum / chunk.points.size();
        chunk.min = chunk.points.stream().mapToDouble(Point::getValue).min().getAsDouble();
        chunk.max = chunk.points.stream().mapToDouble(Point::getValue).max().getAsDouble();
        chunk.name = "temp_a";
        chunk.sax = "edeebcccdf";
        return chunk;
    }

    private ChunkExpected buildChunk2() {
        ChunkExpected chunk = new ChunkExpected();
        chunk.points = Arrays.asList(
                new Point(0, 5L, -2),
                new Point(0, 6L, 8.8),
                new Point(0, 7L, 13.3),
                new Point(0, 8L, 2)
        );
        chunk.compressedPoints = compressPoints(chunk.points);
        chunk.start = 5L;
        chunk.end = 8L;
        chunk.sum = chunk.points.stream().mapToDouble(Point::getValue).sum();
        chunk.avg = chunk.sum / chunk.points.size();
        chunk.min = chunk.points.stream().mapToDouble(Point::getValue).min().getAsDouble();
        chunk.max = chunk.points.stream().mapToDouble(Point::getValue).max().getAsDouble();
        chunk.name = "temp_a";
        chunk.sax = "edeebcccdf";
        return chunk;
    }

    private ChunkExpected buildChunk3() {
        ChunkExpected chunk = new ChunkExpected();
        chunk.points = Arrays.asList(
                new Point(0, 9L, -5),
                new Point(0, 10L, 80),
                new Point(0, 11L, 1.2),
                new Point(0, 12L, 5.5)
        );
        chunk.compressedPoints = compressPoints(chunk.points);
        chunk.start = 9L;
        chunk.end = 12L;
        chunk.sum = chunk.points.stream().mapToDouble(Point::getValue).sum();
        chunk.avg = chunk.sum / chunk.points.size();
        chunk.min = chunk.points.stream().mapToDouble(Point::getValue).min().getAsDouble();
        chunk.max = chunk.points.stream().mapToDouble(Point::getValue).max().getAsDouble();
        chunk.name = "temp_a";
        chunk.sax = "edeebcccdf";
        return chunk;
    }
}
