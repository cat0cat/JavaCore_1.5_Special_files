import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        //Задача 1: CSV - JSON парсер
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        String jsonFilename = "data.json";
        writeString(json, jsonFilename);

        //Задача 2: XML - JSON парсер
        String xmlName = "data.xml";
        List<Employee> list2 = parseXML(xmlName);
        String json2 = listToJson(list2);
        String jsonFilename2 = "data2.json";
        writeString(json2, jsonFilename2);

        //Задача 3: JSON парсер (со звездочкой *)
        String jsonName = "new_data.json";
        String jsonFilename3 = readString(jsonName);
        List<Employee> list3 = jsonToList(jsonFilename3);
        list3.forEach(System.out::println);
    }

    private static List<Employee> jsonToList(String jsonName) {
        List<Employee> list = new ArrayList<>();
        JSONParser parser = new JSONParser();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            Object obj = parser.parse(jsonName);
            JSONArray jsonArray = (JSONArray) obj;
            for (Object o: jsonArray) {
                list.add(gson.fromJson(o.toString(),Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String readString(String jsonName) {
        // Метод должен возвращать прочитанный из файла JSON типа String.
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonName))) {
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s).append("\n");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return sb.toString();

    }

    private static List<Employee> parseXML(String xmlName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();
        List<String> elements = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(xmlName));
        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeName().equals("employee")) {
                NodeList nodeList1 = node.getChildNodes();
                for (int j = 0; j < nodeList1.getLength(); j++) {
                    Node node1 = nodeList1.item(j);
                    if (Node.ELEMENT_NODE == node1.getNodeType()) {
                        elements.add(node1.getTextContent());
                    }
                }
                list.add(new Employee(
                        Long.parseLong(elements.get(0)),
                        elements.get(1),
                        elements.get(2),
                        elements.get(3),
                        Integer.parseInt(elements.get(4))));
                elements.clear();
            }
        }
        return list;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try(CSVReader cvsReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean <Employee> csvToBean = new CsvToBeanBuilder <Employee> (cvsReader)
                    .withMappingStrategy(strategy)
                    .build();
            return csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Полученный список преобразуем в строчку в формате JSON
    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        return gson.toJson(list, listType);
    }

    // запись полученного JSON в файл
    private static void writeString(String json, String jsonFileName) {
        try(FileWriter file = new FileWriter(jsonFileName)) {
            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
