package gptcode;
import javax.swing.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import gptcode.themes.MyFlatLaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.*;
 

public class SwingUI extends JFrame {
    private JTextField textField;
    private JButton uploadButton;
    private JButton sendButton;
    private JTextArea textArea;
    private JScrollPane scrollPane;

    private JPanel labelPanel;
    private JLabel modelLabel;
    private JLabel modelValueLabel;
    private JLabel usageLabel;
    private JLabel usageValueLabel;
    private JLabel promptTkLabel;
    private JLabel promptTkValueLabel;
    private JLabel completionTkLabel;
    private JLabel completionTkValueLabel;
    private JLabel totalTkLabel;
    private JLabel totalTkValueLabel;

    private List<String> systemStr = new ArrayList<>();
    private List<String> userStr = new ArrayList<>();
    private List<String> assistantStr = new ArrayList<>();

    private static final String API_KEY = "OEPNAI_API_KEY";
    private OkHttpClient client;

    private static int promptTokens;
    private static int completionTokens;
    private static int totalTokens;

    private String modelStr;

    public SwingUI() {
        setTitle("Swing ChatGpt");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        textField = new JTextField(20);
        
        uploadButton = new JButton("File");
        sendButton = new JButton("Enter");

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(textField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(uploadButton, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        topPanel.add(sendButton, gbc);

        textArea = new JTextArea(30, 50);
        scrollPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textArea.setLineWrap(true);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        modelStr = "gpt-3.5-turbo";

         labelPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
         modelLabel = new JLabel("model:");
         modelValueLabel = new JLabel(modelStr);
         usageLabel = new JLabel("      |");
         
         promptTkLabel = new JLabel("prompt:");
         promptTkValueLabel = new JLabel(promptTokens == 0 ? "--" : String.valueOf(promptTokens));
         completionTkLabel = new JLabel("completion:");
         completionTkValueLabel = new JLabel(completionTokens == 0 ? "--" : String.valueOf(completionTokens));
         totalTkLabel = new JLabel("total:");
         totalTkValueLabel = new JLabel(totalTokens == 0 ? "--" : String.valueOf(totalTokens));
         usageValueLabel = new JLabel("(tokens)");
        labelPanel.add(modelLabel);
        labelPanel.add(modelValueLabel);
        labelPanel.add(usageLabel);
       
        labelPanel.add(promptTkLabel);
        labelPanel.add(promptTkValueLabel);
        labelPanel.add(completionTkLabel);
        labelPanel.add(completionTkValueLabel);
        labelPanel.add(totalTkLabel);
        labelPanel.add(totalTkValueLabel); 
         labelPanel.add(usageValueLabel);

        add(labelPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String response = sendQuestionToOpenAI(textField.getText());
                    textArea.setText(response);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        //엔터키를 입력하면, 자동으로 문장이 입력되도록 하는 부분.
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });
         client = new OkHttpClient.Builder()
    .connectTimeout(120, TimeUnit.SECONDS)
    .writeTimeout(120, TimeUnit.SECONDS)
    .readTimeout(120, TimeUnit.SECONDS)
    .build();

        setBounds(100, 100, 450, 300);
        setVisible(true);

        new UsageScreen();
        textField.requestFocus();
    }

    public String sendQuestionToOpenAI(String question) throws IOException, NullPointerException {
        System.out.println();
        System.out.println();
        System.out.println();
    System.out.println("Saved Data start--------------------------------------------------------------------------");
         StringBuilder messages = new StringBuilder();

        if(systemStr ==null || systemStr.size()==0){
            systemStr.add("You are a helpful assistant.");
             messages.append("{\"role\": \"system\", \"content\": \""+systemStr.get(0)+"\"},");
            userStr.add(question);
            assistantStr.add("");
            messages.append("{\"role\": \"user\", \"content\": \""+userStr.get(0)+"\"},");
             messages.append("{\"role\": \"assistant\", \"content\": \""+assistantStr.get(0)+"\"},");
        }else{
           
         
        }
        
        
       
        for(int i=0; i<userStr.size(); i++){
            if(assistantStr ==null || assistantStr.size()==0){
                
            }else{
               
            messages.append("{\"role\": \"assistant\", \"content\": \""+assistantStr.get(i)+"\"},");
           
            messages.append("{\"role\": \"user\", \"content\": \""+userStr.get(i)+"\"},");
            }
           
         
            System.out.println(i+" assi : " +assistantStr.get(i)+" \n");
            System.out.println(i+ " user : " +userStr.get(i)+"\n");
            
        }
        userStr.add(question);
         messages.append("{\"role\": \"user\", \"content\": \""+userStr.get(userStr.size()-1)+"\"},");
        System.out.println("Saved Data end -----------------------------------------------------------------------");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("now    :" + question);
                System.out.println();
        System.out.println();
        System.out.println("waiting....\n");
        // 마지막 쉼표 제거
        if (messages.length() > 0) {
            messages.deleteCharAt(messages.length() - 1);
        }

        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        String json = "{"
                  + "\"model\": \""+modelStr+"\","
                + "\"messages\": ["
                + messages.toString() 
                + "]"
                + "}";

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
          
              JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseBody);
            JsonObject obj = element.getAsJsonObject();
            String content = null;
            if( obj.getAsJsonArray("choices")!=null){
            JsonArray choices = obj.getAsJsonArray("choices");
            JsonObject choice = choices.get(0).getAsJsonObject();
            JsonObject message = choice.getAsJsonObject("message");
             content = message.get("content").getAsString();
             JsonObject usage = obj.getAsJsonObject("usage");
            promptTokens = usage.get("prompt_tokens").getAsInt();
            promptTkValueLabel.setText(Integer.toString(promptTokens));
            completionTokens = usage.get("completion_tokens").getAsInt();
            completionTkValueLabel.setText(Integer.toString(completionTokens));
            totalTokens = usage.get("total_tokens").getAsInt();
            totalTkValueLabel.setText(Integer.toString(totalTokens));
            Utilv1.times++;
            Utilv1.completionv = completionTokens;
            Utilv1.promptv = promptTokens;
            Utilv1.totalv = totalTokens;
            if(Utilv1.contents == null){
                Utilv1.contents = ("times:\tcompletion\tprompt\ttotal\n");
                 
                Utilv1.setContents(Utilv1.contents + Utilv1.times + "\t" + Utilv1.completionv + "\t" + Utilv1.promptv + "\t" + Utilv1.totalv + "\n");
            }else{
                 
                Utilv1.setContents(Utilv1.contents + Utilv1.times + "\t" + Utilv1.completionv + "\t" + Utilv1.promptv + "\t" + Utilv1.totalv + "\n");
            }  
            //userStr.add(question);
               assistantStr.add(content);
             System.out.println(assistantStr.size() +" assi : "+ assistantStr.get(assistantStr.size()-1)); 
             System.out.println();
             System.out.println(responseBody);
         
            }else{
                 //userStr.add(question);
            //systemStr.add("no response");
            System.out.println("no response");
            }
            if(content ==null){
                content = "no response";
            }
            return content+"\n";
        }

       
    }

    public static void main(String[] args) {
        MyFlatLaf.setup();
        new SwingUI();
    }
}

/*
model,  
https://platform.openai.com/account/rate-limits
*/
 
