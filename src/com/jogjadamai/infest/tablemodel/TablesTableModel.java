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
 * <h1>class <code>TablesTableModel</code></h1>
 * <p><code>TablesTableModel</code> is <code>javax.swing.table.AbstractTableModel</code>
 * class defining the table model of <code>Report</code> entity.</p>
 * <br>
 * <p><b><i>Coded, built, and packaged with passion by Adam Afandi for Infest.</i></b></p>
 * 
 * @author Adam Afandi
 * @version 2017.03.10.0001
 */
public final class TablesTableModel extends javax.swing.table.AbstractTableModel {

    private final java.util.List<com.jogjadamai.infest.entity.Tables> TABLES_LIST;

    public TablesTableModel(java.util.List<com.jogjadamai.infest.entity.Tables> tableList) {
        this.TABLES_LIST = tableList;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        return this.TABLES_LIST.size();
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Table ID";
            case 1:
                return "Name";
            case 2:
                return "Description";
            default:
                return null;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        switch (column) {
            case 0:
                return this.TABLES_LIST.get(row).getId();
            case 1:
                return this.TABLES_LIST.get(row).getName();
            case 2:
                return this.TABLES_LIST.get(row).getDescription();
            default:
                return null;
        }
    }
}
