package bajaj.problem2;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class AppTest 
{

    private static final String BASE_URL = "https://bfhldevapigw.healthrx.co.in/automation-campus/create/user";
    private static final String VALID_ROLL_NUMBER = "1";
    
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }
//************************Set 1 ***********************************************
    // Positive Test Case: Valid User Creation
    @Test
    public void testValidUserCreation() {
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body("{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"phoneNumber\": 1234567890, \"emailId\": \"john.doe@example.com\" }")
        .when()
            .post()
        .then()
            .statusCode(200)  // or 201 depending on the API response
            .body("message", containsString("User created successfully"));
    }

    // Positive Test Case: Valid Multiple User Creations
    @Test
    public void testMultipleUserCreation() {
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body("{ \"firstName\": \"Jane\", \"lastName\": \"Doe\", \"phoneNumber\": 2345678901, \"emailId\": \"jane.doe@example.com\" }")
        .when()
            .post()
        .then()
            .statusCode(200)  // or 201 depending on the API response
            .body("message", containsString("User created successfully"));
    }
    
  //************************Set 2 ***********************************************
 // Negative Test Case: Duplicate Phone Number
    @Test
    public void testDuplicatePhoneNumber() {
        String payload = "{ \"firstName\": \"Jane\", \"lastName\": \"Doe\", \"phoneNumber\": 1234567890, \"emailId\": \"jane.doe@example.com\" }";

        // First, create a user with the phone number
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(200); // Ensure the user is created first

        // Now, attempt to create another user with the same phone number
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", containsString("duplicate phone number"));
    }

    // Negative Test Case: Missing Roll Number
    @Test
    public void testMissingRollNumber() {
        given()
            .contentType(ContentType.JSON)
            .body("{ \"firstName\": \"Alex\", \"lastName\": \"Smith\", \"phoneNumber\": 9876543210, \"emailId\": \"alex.smith@example.com\" }")
        .when()
            .post()
        .then()
            .statusCode(401)
            .body("error", containsString("roll number is required"));
    }

    // Negative Test Case: Duplicate Email ID
    @Test
    public void testDuplicateEmailId() {
        String payload = "{ \"firstName\": \"Mike\", \"lastName\": \"Ross\", \"phoneNumber\": 2345678901, \"emailId\": \"mike.ross@example.com\" }";

        // First, create a user with the email ID
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(200); // Ensure the user is created first

        // Now, attempt to create another user with the same email ID
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body(payload)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", containsString("duplicate email"));
    }

 // Negative Test Case: Invalid Email Format
    @Test
    public void testInvalidEmailFormat() {
        given()
            .header("roll-number", VALID_ROLL_NUMBER)
            .contentType(ContentType.JSON)
            .body("{ \"firstName\": \"Chris\", \"lastName\": \"Evans\", \"phoneNumber\": 5678901234, \"emailId\": \"invalid-email-format\" }")
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", containsString("invalid email format"));
    }

}
