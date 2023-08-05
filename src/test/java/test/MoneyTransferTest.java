package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import page.DashboardPage;
import page.LoginPage;


import static com.codeborne.selenide.Selenide.open;
import static data.DataHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MoneyTransferTest {
    DashboardPage dashboardPage;

    @BeforeEach
    void setup() {
        var loginPage = open("http://localhost:9999/", LoginPage.class);
        var authInfo = getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

   @Test
    void testTransferFromFirst() {
        var firstCard = getFirstCard();
        var secondCard = getSecondCard();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);
        var amount = generateValidAmount(firstCardBalance);
        var expectedFirstCardBalance = firstCardBalance - amount;
        var expectedSecondCardBalance = secondCardBalance + amount;
        var transferPage = dashboardPage.selectCardTransfer(secondCard);
        dashboardPage = transferPage.validTransfer(String.valueOf(amount), firstCard);
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        assertEquals(expectedFirstCardBalance, actualFirstCardBalance);
        assertEquals(expectedSecondCardBalance, actualSecondCardBalance);
    }

   @Test
    void testErrorMessage() {
        var firstCard = getFirstCard();
        var secondCard = getSecondCard();
        var firstCardBalance = dashboardPage.getCardBalance(firstCard);
        var secondCardBalance = dashboardPage.getCardBalance(secondCard);
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashboardPage.selectCardTransfer(firstCard);
        transferPage.transfer(String.valueOf(amount), secondCard);
        transferPage.findErrorMessage("Выполнена попытка перевода суммы, превышающей остаток на карте списания");
        var actualFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        var actualSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        assertEquals(firstCardBalance, actualFirstCardBalance);
        assertEquals(secondCardBalance, actualSecondCardBalance);
    }
}
