import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);

        String jsonFileName = "data.json";
        writeString(jsonFileName, json);

        String xmlFile = "data.xml";
        List<Employee> employeesXml = parseXML(xmlFile);

        String xmlJson = listToJson(employeesXml);

        String xmlJsonFileName = "data2.json";
        writeString(xmlJsonFileName, xmlJson);
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) throws IOException {
        try(FileReader reader = new FileReader(fileName)) {
            ColumnPositionMappingStrategy columnPositionMappingStrategy = new ColumnPositionMappingStrategy();
            columnPositionMappingStrategy.setType(Employee.class);
            columnPositionMappingStrategy.setColumnMapping(columnMapping);

            CsvToBeanBuilder csvToBeanBuilder = new CsvToBeanBuilder(reader);
            csvToBeanBuilder.withMappingStrategy(columnPositionMappingStrategy);

            CsvToBean<Employee> csvToBean = csvToBeanBuilder.build();

            List<Employee> employees = csvToBean.parse();
            return employees;
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();

        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> result = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(fileName);

        Element root = document.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("employee");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element)nodeList.item(i);

            String id =  element.getElementsByTagName("id").item(0).getFirstChild().getNodeValue();
            String firstName = element.getElementsByTagName("firstName").item(0).getFirstChild().getNodeValue();
            String lastName = element.getElementsByTagName("lastName").item(0).getFirstChild().getNodeValue();
            String country = element.getElementsByTagName("country").item(0).getFirstChild().getNodeValue();
            String age = element.getElementsByTagName("age").item(0).getFirstChild().getNodeValue();

            Employee employee = new Employee(Long.parseLong(id), firstName, lastName, country, Integer.parseInt(age));
            result.add(employee);
        }

        return result;
    }


    private static void writeString(String fileName, String json) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(json);
        }
    }
}
