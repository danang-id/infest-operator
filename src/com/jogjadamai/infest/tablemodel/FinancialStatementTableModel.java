/*
 * Copyright 2017 Adam Afandi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jogjadamai.infest.tablemodel;

/**
 * <h1>class <code>FinancialStatementTableModel</code></h1>
 * <p><code>FinancialStatementTableModel</code> is <code>javax.swing.table.AbstractTableModel</code>
 * class defining the table model of <code>Report</code> entity.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Adam Afandi for Infest.</i></b></p>
 * 
 * @author Adam Afandi
 * @version 2017.03.10.0001
 */
public final class FinancialStatementTableModel extends javax.swing.table.AbstractTableModel {

    private final java.util.List<com.jogjadamai.infest.entity.FinanceReport> FINANCE_REPORT_LIST;
    private final String CURRENCY;

    public FinancialStatementTableModel(
            java.util.List<com.jogjadamai.infest.entity.FinanceReport> financeReportList, 
            com.jogjadamai.infest.entity.Features currency) {
        this.FINANCE_REPORT_LIST = financeReportList;
        if(currency.getStatus() == 1) this.CURRENCY = " [" + currency.getDescription() + "]";
        else this.CURRENCY = "";
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public int getRowCount() {
        return this.FINANCE_REPORT_LIST.size();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Menu Name";
            case 1:
                return "Menu Price" + this.CURRENCY;
            case 2:
                return "Total Order [unit(s)]";
            case 3:
                return "Income" + this.CURRENCY;
            case 4:
                return "Status";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        java.text.Format dateFormat = new java.text.SimpleDateFormat("dd MMMM yyyy");
        switch (column) {
            case 0:
                return this.FINANCE_REPORT_LIST.get(row).getMenuName();
            case 1:
                return this.FINANCE_REPORT_LIST.get(row).getMenuPrice();
            case 2:
                return this.FINANCE_REPORT_LIST.get(row).getOrderTotal();
            case 3:
                return this.FINANCE_REPORT_LIST.get(row).getIncome();
            case 4:
                switch(this.FINANCE_REPORT_LIST.get(row).getMenuStatus()) {
                    case 0:
                        return "Off the Market (since " + dateFormat.format(this.FINANCE_REPORT_LIST.get(row).getMenuStatusdate()) + ")";
                    case 1:
                        return "On the Market (since " + dateFormat.format(this.FINANCE_REPORT_LIST.get(row).getMenuStatusdate()) + ")";
                    default:
                        break;
                }
            default:
                return null;
        }
    }
}