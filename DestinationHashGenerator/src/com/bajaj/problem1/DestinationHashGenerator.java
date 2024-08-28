package com.bajaj.problem1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class DestinationHashGenerator {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN> <path to JSON file>");
            return;
        }

        String prn = args[0].toLowerCase();
        String jsonFilePath = args[1];

        try {
            // Parse the JSON file
            File jsonFile = new File(jsonFilePath);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile);

            // Traverse the JSON to find the "destination" key
            String destinationValue = findDestinationValue(rootNode);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate a random alphanumeric string of 8 characters
            String randomString = generateRandomString(8);

            // Concatenate PRN, destination value, and the random string
            String concatenatedString = prn + destinationValue + randomString;

            // Generate the MD5 hash of the concatenated string
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the result in the format <hashed value>;<random string>
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error generating MD5 hash: " + e.getMessage());
        }
    }

    private static String findDestinationValue(JsonNode node) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (field.getKey().equals("destination")) {
                    return field.getValue().asText();
                } else {
                    String result = findDestinationValue(field.getValue());
                    if (result != null) return result;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                String result = findDestinationValue(arrayElement);
                if (result != null) return result;
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
