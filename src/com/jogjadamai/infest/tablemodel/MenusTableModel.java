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
 * <h1>class <code>MenusTableModel</code></h1>
 * <p><code>MenusTableModel</code> is <code>javax.swing.table.AbstractTableModel</code>
 * class defining the table model of <code>Menus</code> entity.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Adam Afandi for Infest.</i></b></p>
 * 
 * @author Adam Afandi
 * @version 2017.03.10.0001
 */
public class MenusTableModel extends javax.swing.table.AbstractTableModel {

    private final java.util.List<com.jogjadamai.infest.entity.Menus> MENUS_LIST;
    private final String CURRENCY;

    public MenusTableModel(
            java.util.List<com.jogjadamai.infest.entity.Menus> menuList,
            com.jogjadamai.infest.entity.Features currency) {
        this.MENUS_LIST = menuList;
        if(currency.getStatus() == 1) this.CURRENCY = " [" + currency.getDescription() + "]";
        else this.CURRENCY = "";
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public int getRowCount() {
        return this.MENUS_LIST.size();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Name";
            case 1:
                return "Status";
            case 2:
                return "Type";
            case 3:
                return "Price" + this.CURRENCY;
            case 4:
                return "Stock [unit(s)]";
            case 5:
                return "Duration";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        java.text.Format dateFormat = new java.text.SimpleDateFormat("dd MMMM yyyy");
        java.text.Format timeFormat = new java.text.SimpleDateFormat("HH:mm");
        switch (column) {
            case 0:
                return this.MENUS_LIST.get(row).getName();
            case 1:
                switch(this.MENUS_LIST.get(row).getStatus()) {
                    case 0:
                        return "OFF Market (" + dateFormat.format(this.MENUS_LIST.get(row).getStatusDate()) + ")";
                    case 1:
                        return "ON Market (" + dateFormat.format(this.MENUS_LIST.get(row).getStatusDate()) + ")";
                    default:
                        break;
                }
            case 2:
                switch (this.MENUS_LIST.get(row).getType()) {
                    case 0:
                        return "OTHER / NOT DISPLAYED";
                    case 1:
                        return "APPETIZER";
                    case 2:
                        return "MAIN COURSE";
                    case 3:
                        return "DESSERT";
                    case 4:
                        return "BEVERAGES";
                    default:
                        break;
                }
            case 3:
                return this.MENUS_LIST.get(row).getPrice();
            case 4:
                return this.MENUS_LIST.get(row).getStock();
            case 5:
                return timeFormat.format(this.MENUS_LIST.get(row).getDuration());
            default:
                return null;
        }
    }
}