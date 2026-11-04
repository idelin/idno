package win.delin.idcards;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import win.delin.idcards.Exception.IdCardCheckCodeErrorException;
import win.delin.idcards.Exception.IdCardException;

/**
 * @author delin
 * @date 2020/1/14
 */
public class IdCardUtilsTest {

    private static final String INVALID_CHECK_CODE_CARD = "220202202002022222";

    @DataProvider(name = "idCards")
    public Object[][] getIdCards() {
        return new String[][]{
                {"220202202002022220"}
        };
    }

    @Test(dataProvider = "idCards")
    public void testGetInformation(String idCard) throws IdCardException {
        IdCard idCard1 = IdCardUtils.getInformation(idCard);
        System.out.println(idCard1);
        IdCard idCard2 = IdCardUtils.getInformation(idCard, true);
        System.out.println(idCard2);
    }

    @Test(dataProvider = "idCards")
    public void testValidateCard(String idCard) throws IdCardException {
        System.out.println(IdCardUtils.validateCard(idCard));
    }

    @Test(expectedExceptions = IdCardCheckCodeErrorException.class,
            expectedExceptionsMessageRegExp = "cardNo is \\[220202202002022222\\], realCheckCode must \\[0\\]")
    public void testGetInformationRejectsBadCheckCode() throws IdCardException {
        IdCardUtils.getInformation(INVALID_CHECK_CODE_CARD);
    }

    @Test(expectedExceptions = IdCardCheckCodeErrorException.class,
            expectedExceptionsMessageRegExp = "cardNo is \\[220202202002022222\\], realCheckCode must \\[0\\]")
    public void testValidateCardRejectsBadCheckCode() throws IdCardException {
        IdCardUtils.validateCard(INVALID_CHECK_CODE_CARD);
    }
}
