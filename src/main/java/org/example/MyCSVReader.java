package org.example;

import com.opencsv.CSVReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.List;

public class MyCSVReader {
    public static void main(String[] args) {
        // Path to the CSV file
        String filePath = "csv/kodutoo_1.csv";

        // Dataset for chart values
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Variables to calculate average establishment times
        int MoCRowCount = 0;
        int MtCRowCount = 0;
        double MoCEstablishmentTimeSum = 0;
        double MtCEstablishmentTimeSum = 0;

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            List<String[]> data = reader.readAll();

            for (String[] row : data) {
                // Split the CSV data into rows and columns
                String csvRow = String.join(";", row); // Convert the String array to a single CSV line
                String[] rowArray = csvRow.split(";(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                // Ensure that rowArray has 10 elements by adding missing "0"
                if (rowArray.length < 10) {
                    String[] tempArray = new String[10];
                    System.arraycopy(rowArray, 0, tempArray, 0, rowArray.length);
                    for (int i = rowArray.length; i < 10; i++) {
                        tempArray[i] = "0";
                    }
                    rowArray = tempArray;
                }

                // Calculate the sum of establishment times for MoC and MtC rows
                if (rowArray.length >= 10) {
                    double MoCEstablishmentTime = 0;
                    double MtCEstablishmentTime = 0;

                    if (!rowArray[4].isBlank()) {
                        MoCEstablishmentTime = Double.parseDouble(rowArray[4].replace(',', '.'));
                    }

                    if (!rowArray[5].isBlank()) {
                        MtCEstablishmentTime = Double.parseDouble(rowArray[5].replace(',', '.'));
                    }
                    if (MoCEstablishmentTime != 0 && !Double.isNaN(MoCEstablishmentTime)) {
                        MoCEstablishmentTimeSum += MoCEstablishmentTime;
                        MoCRowCount++;
                    }
                    if (MtCEstablishmentTime != 0 && !Double.isNaN(MoCEstablishmentTime)) {
                        MtCEstablishmentTimeSum += MtCEstablishmentTime;
                        MtCRowCount++;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calculate the average establishment times for MoC and MtC
        double MoCEstablishmentTimeAvg = MoCRowCount > 0 ? MoCEstablishmentTimeSum / MoCRowCount : 0;
        double MtCEstablishmentTimeAvg = MtCRowCount > 0 ? MtCEstablishmentTimeSum / MtCRowCount : 0;

        // Adding MoC and MtC average establishment times to the dataset
        dataset.addValue(MoCEstablishmentTimeAvg, "MoC", "MoC Establishment Time Avg");
        dataset.addValue(MtCEstablishmentTimeAvg, "MtC", "MtC Establishment Time Avg");

        // Create a column chart
        JFreeChart columnChart = ChartFactory.createBarChart(

                "Average Establishment Times", // Chart title
                "",      // X-axis label
                "Average time (s)",      // Y-axis label
                dataset,             // Dataset
                PlotOrientation.VERTICAL, // Orientation
                true,                // Include legend
                true,                // Include tooltips
                false                // Include URLs
        );

        // Set font sizes for the title and axes
        columnChart.getTitle().setFont(new Font("SansSerif", Font.BOLD, 28)); // Set title font and size
        columnChart.getCategoryPlot().getDomainAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 22)); // X-axis label font and size
        columnChart.getCategoryPlot().getRangeAxis().setLabelFont(new Font("SansSerif", Font.PLAIN, 22)); // Y-axis label font and size
        columnChart.getLegend().setItemFont(new Font("SansSerif", Font.PLAIN, 22)); // Set legend font and size

        // Set font size for the category labels (column keys)
        CategoryPlot plot = columnChart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 22)); // Set category label (column key) font and size

        // Create a DecimalFormat object to format the doubles
        DecimalFormat df = new DecimalFormat("0.00");

        // Add annotations for displaying the average establishment times
        CategoryTextAnnotation mocAnnotation = new CategoryTextAnnotation(
                "MoC: " + df.format(MoCEstablishmentTimeAvg), "MoC Establishment Time Avg", MoCEstablishmentTimeAvg);
        CategoryTextAnnotation mtcAnnotation = new CategoryTextAnnotation(
                "MtC: " + df.format(MtCEstablishmentTimeAvg), "MtC Establishment Time Avg", MtCEstablishmentTimeAvg);

        // Adjust the font size
        mocAnnotation.setFont(new Font("SansSerif", Font.PLAIN, 30));
        mtcAnnotation.setFont(new Font("SansSerif", Font.PLAIN, 30));

        plot.addAnnotation(mocAnnotation);
        plot.addAnnotation(mtcAnnotation);

        // Display the chart in a frame
        ChartPanel chartPanel = new ChartPanel(columnChart);
        JFrame frame = new JFrame("CSV Data Visualization");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }
}