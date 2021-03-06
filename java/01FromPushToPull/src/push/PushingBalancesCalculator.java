package push;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

import common.BalancesOfMonthCalculator_API;
import common.BalancesOfMonth_API;
import common.Transaction_API;

public class PushingBalancesCalculator implements BalancesOfMonthCalculator_API {

	private final List<Transaction_API> transactions;

	public PushingBalancesCalculator(List<Transaction_API> transactions) {
		this.transactions = transactions;
	}

	@Override
	public void fillData(List<BalancesOfMonth_API> balancesOfMonthList) {
		ValuesOfMonth valuesOfMonth = new ValuesOfMonth();
		int balance = 0;

		for (BalancesOfMonth_API balancesOfMonth : balancesOfMonthList) {
			int ultimo = balancesOfMonth.getDate().getDayOfMonth();

			double averageBalance = 0;
			int dayOfLatestBalance = 1;
			List<Transaction_API> transactionsOfMonth = transactionsOfMonth(balancesOfMonth.getDate());
			for (Transaction_API transaction : transactionsOfMonth) {
				int day = transaction.getDate().getDayOfMonth();
				averageBalance += calculateProportionalBalance(dayOfLatestBalance, balance, day, ultimo);
				balance += transaction.getAmount();
				dayOfLatestBalance = day;
			}
			averageBalance += calculateProportionalBalance(dayOfLatestBalance, balance, ultimo + 1, ultimo);

			valuesOfMonth.setBalanceAndAverage(balance, averageBalance);

			balancesOfMonth.setBalance(valuesOfMonth.getBalance());
			balancesOfMonth.setAverageBalance(valuesOfMonth.getAverageBalance());
		}
	}

	private double calculateProportionalBalance(int dayOfLatestBalance, int balance, int day, int daysInMonth) {
		int countingDays = day - dayOfLatestBalance;
		if (countingDays == 0) {
			return 0;
		}
		double rate = (double) countingDays / daysInMonth;
		return (balance * rate);
	}

	private List<Transaction_API> transactionsOfMonth(LocalDate date) {
		List<Transaction_API> results = new ArrayList<Transaction_API>();
		for (Transaction_API transaction : transactions) {
			LocalDate dateOfTransaction = transaction.getDate();
			if (areSameMonthAndYear(date, dateOfTransaction)) {
				results.add(transaction);
			}
		}
		return results;
	}

	private boolean areSameMonthAndYear(LocalDate date, LocalDate dateOfTransaction) {
		return dateOfTransaction.getMonthOfYear() == date.getMonthOfYear() && dateOfTransaction.getYear() == date.getYear();
	}

}
