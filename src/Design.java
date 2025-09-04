import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;   // fixed: was EncodingUtils (wrong)
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Design extends JFrame {

    Design() {
        String[] currencies = {"USD", "INR", "EUR", "GBP", "JPY"};
        JComboBox<String> fromCurrency = new JComboBox<>(currencies);
        JComboBox<String> toCurrency = new JComboBox<>(currencies);

        JTextField amountField = new JTextField(10);
        JButton convertBtn = new JButton("Convert");
        JLabel resultLabel = new JLabel("Result: ");

        JPanel jPanel = new JPanel();
        jPanel.add(new JLabel("From: "));
        jPanel.add(fromCurrency);
        jPanel.add(new JLabel("To: "));
        jPanel.add(toCurrency);
        jPanel.add(new JLabel("Amount: "));
        jPanel.add(amountField);
        jPanel.add(convertBtn);
        jPanel.add(resultLabel);

        add(jPanel);
        setTitle("Currency Converter");
        setSize(560, 480);
        setLocation(350, 200);
        getContentPane().setBackground(new Color(204, 228, 230));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        convertBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String from = (String) fromCurrency.getSelectedItem();
                String to = (String) toCurrency.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                double result = getConversion(from, to, amount);
                resultLabel.setText("Result: " + result + " " + to);
            }
        });
    }

    private double getConversion(String from, String to, double amount) {
        double result = 0.0;
        try {
            String urlStr = "https://api.frankfurter.app/latest?amount=" + amount
                    + "&from=" + from + "&to=" + to;

            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(urlStr);
            CloseableHttpResponse response = client.execute(request);

            String json = EntityUtils.toString(response.getEntity());
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            // Navigate to rates → { "INR": 8803 }
            JsonObject rates = jsonObject.getAsJsonObject("rates");
            result = rates.get(to).getAsDouble();

            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    public static void main(String[] args) {
        new Design();
    }
}
