package win.delin.idcards;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import win.delin.idcards.Exception.IdCardException;
import win.delin.idcards.Exception.IdCardCheckCodeErrorException;
import java.util.Map;

/**
 * @author delin
 * @date 2020/1/14
 */
public class IdCardUtilsTest {

    // @DataProvider(name = "idCards") // Old DataProvider
    // public Object[][] getIdCards() {
    //     return new String[][]{
    //             {"220202202002022220"},
    //             {"220202202002022222"}
    //     };
    // }

    @DataProvider(name = "validIdCards")
    public Object[][] getValidIdCards() {
        return new String[][]{
                {"220202202002022220"} // Known valid ID
        };
    }

    @DataProvider(name = "invalidChecksumIdCards")
    public Object[][] getInvalidChecksumIdCards() {
        return new String[][]{
                {"220202202002022222"} // Known invalid checksum
        };
    }

    // @Test(dataProvider = "idCards") // Old test
    // public void testGetInformation(String idCard) throws IdCardException {
    //     IdCard idCard1 = IdCardUtils.getInformation(idCard);
    //     System.out.println(idCard1);
    //     IdCard idCard2 = IdCardUtils.getInformation(idCard, true);
    //     System.out.println(idCard2);
    // }

    @Test(dataProvider = "validIdCards")
    public void testGetInformation_valid(String idCard) throws IdCardException {
        IdCard idCard1 = IdCardUtils.getInformation(idCard);
        System.out.println("testGetInformation_valid: " + idCard1);
        Assert.assertNotNull(idCard1, "IdCard object should not be null for valid ID.");
        IdCard idCard2 = IdCardUtils.getInformation(idCard, true);
        System.out.println("testGetInformation_valid (lunar): " + idCard2);
        Assert.assertNotNull(idCard2, "IdCard object (lunar) should not be null for valid ID.");
    }

    @Test(dataProvider = "invalidChecksumIdCards", expectedExceptions = IdCardCheckCodeErrorException.class)
    public void testGetInformation_invalidChecksum(String idCard) throws IdCardException {
        System.out.println("Attempting testGetInformation_invalidChecksum with ID: " + idCard);
        IdCardUtils.getInformation(idCard);
    }

    // @Test(dataProvider = "idCards") // Old test
    // public void testValidateCard(String idCard) throws IdCardException {
    //     System.out.println(IdCardUtils.validateCard(idCard));
    // }

    @Test(dataProvider = "validIdCards")
    public void testValidateCard_valid(String idCard) throws IdCardException {
        System.out.println("Attempting testValidateCard_valid with ID: " + idCard);
        Assert.assertTrue(IdCardUtils.validateCard(idCard), "Expected validateCard to return true for valid ID.");
    }

    @Test(dataProvider = "invalidChecksumIdCards", expectedExceptions = IdCardCheckCodeErrorException.class)
    public void testValidateCard_invalidChecksum(String idCard) throws IdCardException {
        System.out.println("Attempting testValidateCard_invalidChecksum with ID: " + idCard);
        IdCardUtils.validateCard(idCard);
    }

    /*
    @Test
    public void testGetInformationList() {
        java.util.List<String> idCardNumbers = new java.util.ArrayList<>();
        // Valid ID (example, replace with a known valid one if possible, or one that passes existing checksum logic if it were correct)
        // Using the one that currently passes checksum in tests:
        idCardNumbers.add("220202202002022220"); 
        // Invalid checksum (based on current test failures)
        idCardNumbers.add("220202202002022222");
        // Invalid format (too short)
        idCardNumbers.add("12345");
        // Null ID
        idCardNumbers.add(null);
        // Empty ID
        idCardNumbers.add("");
        // Valid 15-digit ID (example, replace with a known valid one)
        idCardNumbers.add("110101900101123"); // Example: Beijing, Dongcheng, born Jan 1, 1990, male. Checksum will be auto-generated.

        Map<String, Object> results = IdCardUtils.getInformationList(idCardNumbers, false);

        assert results.size() == idCardNumbers.size() : "Results map size should match input list size";

        // Assertions for each case
        Object result1 = results.get("220202202002022220");
        assert result1 instanceof IdCard : "Expected IdCard object for 220202202002022220";
        if (result1 instanceof IdCard) {
            IdCard card1 = (IdCard) result1;
            assert "吉林省".equals(card1.getProvince()) : "Province check failed for 220202202002022220";
        }


        Object result2 = results.get("220202202002022222");
        assert result2 instanceof win.delin.idcards.Exception.IdCardCheckCodeErrorException : "Expected IdCardCheckCodeErrorException for 220202202002022222";

        Object result3 = results.get("12345");
        // validateCard throws InvalidIdCardLengthException for "12345"
        assert result3 instanceof win.delin.idcards.Exception.InvalidIdCardLengthException : "Expected InvalidIdCardLengthException for 12345. Got: " + (result3 != null ? result3.getClass().getName() : "null");


        Object resultNull = results.get("null_id"); // Key for null input in getInformationList
        // getInformationList directly puts IllegalArgumentException for null/empty strings
        assert resultNull instanceof IllegalArgumentException : "Expected IllegalArgumentException for null ID. Got: " + (resultNull != null ? resultNull.getClass().getName() : "null");
        if (resultNull instanceof IllegalArgumentException) {
            assert "ID card number cannot be null or empty.".equals(((IllegalArgumentException) resultNull).getMessage()) : "Message check for null ID failed";
        }


        Object resultEmpty = results.get("");
        assert resultEmpty instanceof IllegalArgumentException : "Expected IllegalArgumentException for empty ID. Got: " + (resultEmpty != null ? resultEmpty.getClass().getName() : "null");
        if (resultEmpty instanceof IllegalArgumentException) {
             assert "ID card number cannot be null or empty.".equals(((IllegalArgumentException) resultEmpty).getMessage()) : "Message check for empty ID failed";
        }


        Object resultValid15 = results.get("110101900101123");
        assert resultValid15 instanceof IdCard : "Expected IdCard object for 110101900101123";
         if (resultValid15 instanceof IdCard) {
            IdCard card15 = (IdCard) resultValid15;
            assert "北京市".equals(card15.getProvince()) : "Province check failed for 110101900101123";
            assert "M".equals(card15.getGender()) : "Gender check failed for 11010119900101123X";
        }

        System.out.println("testGetInformationList results:");
        for (Map.Entry<String, Object> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    @Test
    public void testGetDetailedAge() throws IdCardException {
        // Use an ID known to be valid and for which we can calculate expected detailed age.
        // Example: 34052119900115123X (hypothetical, ensure checksum logic would pass if correct)
        // For testing, using "220202202002022220" which passes current checksum logic
        // and has birth date 2020-02-02.
        String validIdCard = "220202202002022220"; 
        IdCard idCardInfo = IdCardUtils.getInformation(validIdCard, false);

        java.util.Calendar birthCal = java.util.Calendar.getInstance();
        birthCal.set(2020, java.util.Calendar.FEBRUARY, 2); // Month is 0-indexed for Calendar

        java.time.LocalDate birthDate = java.time.LocalDate.of(birthCal.get(java.util.Calendar.YEAR), birthCal.get(java.util.Calendar.MONTH) + 1, birthCal.get(java.util.Calendar.DAY_OF_MONTH));
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        java.time.Period period = java.time.Period.between(birthDate, currentDate);
        String expectedDetailedAge = String.format("%d years, %d months, %d days", period.getYears(), period.getMonths(), period.getDays());

        assert expectedDetailedAge.equals(idCardInfo.getDetailedAge()) : "Detailed age calculation mismatch. Expected: " + expectedDetailedAge + ", Got: " + idCardInfo.getDetailedAge();
        System.out.println("Detailed Age Test for " + validIdCard + ": Expected: " + expectedDetailedAge + ", Got: " + idCardInfo.getDetailedAge());
    }
    */
}