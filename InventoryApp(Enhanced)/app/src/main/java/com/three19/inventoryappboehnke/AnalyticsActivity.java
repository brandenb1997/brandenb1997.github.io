package com.three19.inventoryappboehnke;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//Inventory analytics in Pie Chart form
public class AnalyticsActivity extends AppCompatActivity {

    private PieChart pieChart;
    private ListView historyListView;
    private InventoryRepository repo = new InventoryRepository();
    private List<String> history = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);

        pieChart = findViewById(R.id.pieChart);
        historyListView = findViewById(R.id.historyListView);

        Button backButton = findViewById(R.id.backToInventoryButton);
        backButton.setOnClickListener(v -> finish());

        // Load last 10 items into list view
        repo.getLastNItems(10, newItems -> {
            for (Item item : newItems) {
                history.add(item.getName() + " - Qty: " + item.getQuantity());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history);
            historyListView.setAdapter(adapter);
        });

        // Load summary and create pie chart
        repo.getCategorySummary(summary -> {
            List<PieEntry> entries = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : summary.entrySet()) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey()));
            }
        // Configure PieDataSet
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Nice color scheme
            dataSet.setValueTextSize(14f);

            PieData data = new PieData(dataSet);
            pieChart.setData(data);
            pieChart.getDescription().setEnabled(false); // Disable weird label
            pieChart.getLegend().setWordWrapEnabled(true); // Optional: wraps legend if long
            pieChart.invalidate();
        });
    }
}
