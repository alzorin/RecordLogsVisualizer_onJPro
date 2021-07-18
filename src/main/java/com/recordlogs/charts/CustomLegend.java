package com.recordlogs.charts;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.gsi.chart.legend.spi.DefaultLegend;
import de.gsi.chart.renderer.spi.utils.DefaultRenderColorScheme;
import de.gsi.dataset.spi.DefaultDataSet;
import de.gsi.dataset.spi.DoubleDataSet;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import de.gsi.chart.XYChartCss;
import de.gsi.chart.legend.Legend;
import de.gsi.chart.renderer.Renderer;
import de.gsi.chart.utils.StyleParser;
import de.gsi.dataset.DataSet;

/**
 * A customized chart legend that displays a list of items with symbols in a box
 *
 */
public class CustomLegend extends DefaultLegend {
    @Override
    public LegendItem getNewLegendItem(final Renderer renderer, final DataSet series, final int seriesIndex) {
        final Canvas symbol = drawCustomLegendSymbol(series, seriesIndex, 20, 20);
        return new LegendItem(series.getName(), symbol);
    }
    public Canvas drawCustomLegendSymbol(final DataSet dataSet, final int dsIndex, final int width, final int height) {
        final Canvas canvas = new Canvas(width, height);
        final GraphicsContext gc = canvas.getGraphicsContext2D();
        final String style = dataSet.getStyle();

        final Integer layoutOffset = StyleParser.getIntegerPropertyValue(style, XYChartCss.DATASET_LAYOUT_OFFSET);
        final Integer dsIndexLocal = StyleParser.getIntegerPropertyValue(style, XYChartCss.DATASET_INDEX);

        final int dsLayoutIndexOffset = layoutOffset == null ? 0 : layoutOffset;

        final int plottingIndex = dsLayoutIndexOffset + (dsIndexLocal == null ? dsIndex : dsIndexLocal);

        gc.save();

        DefaultRenderColorScheme.setLineScheme(gc, dataSet.getStyle(), plottingIndex);
        DefaultRenderColorScheme.setGraphicsContextAttributes(gc, dataSet.getStyle());

        final double x = width / 2.0;
        final double y = height / 2.0;
        if (dataSet instanceof DefaultDataSet) //Draw cirles for the dotted chart legend
        {
            gc.strokeOval(5, 5, 10, 10);
            gc.setFill(gc.getStroke());
            gc.fillOval(5, 5, 10, 10);
        }
        else {
            gc.strokeLine(1, y, width - 2.0, y); // Draw a line for line chart legend
        }
        gc.restore();
        return canvas;
    }
}
