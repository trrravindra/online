/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2019 Serge Rider (serge@jkiss.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ext.mssql.edit;

import org.jkiss.code.Nullable;
import org.jkiss.dbeaver.ext.mssql.model.SQLServerTable;
import org.jkiss.dbeaver.ext.mssql.model.SQLServerTableColumn;
import org.jkiss.dbeaver.ext.mssql.model.SQLServerTableForeignKey;
import org.jkiss.dbeaver.ext.mssql.model.SQLServerTableForeignKeyColumn;
import org.jkiss.dbeaver.model.edit.DBECommandContext;
import org.jkiss.dbeaver.model.impl.DBSObjectCache;
import org.jkiss.dbeaver.model.impl.sql.edit.struct.SQLForeignKeyManager;
import org.jkiss.dbeaver.model.runtime.DBRProgressMonitor;
import org.jkiss.dbeaver.model.struct.DBSObject;
import org.jkiss.dbeaver.model.struct.rdb.DBSForeignKeyModifyRule;
import org.jkiss.dbeaver.ui.UITask;
import org.jkiss.dbeaver.ui.editors.object.struct.EditForeignKeyPage;

import java.util.Map;

/**
 * SQL Server foreign key manager
 */
public class SQLServerForeignKeyManager extends SQLForeignKeyManager<SQLServerTableForeignKey, SQLServerTable> {

    @Nullable
    @Override
    public DBSObjectCache<? extends DBSObject, SQLServerTableForeignKey> getObjectsCache(SQLServerTableForeignKey object)
    {
        return object.getParentObject().getContainer().getForeignKeyCache();
    }

    @Override
    protected SQLServerTableForeignKey createDatabaseObject(DBRProgressMonitor monitor, DBECommandContext context, final Object table, Object from, Map<String, Object> options)
    {
        final SQLServerTableForeignKey foreignKey = new SQLServerTableForeignKey(
            (SQLServerTable) table,
            null,
            null,
            null,
            DBSForeignKeyModifyRule.NO_ACTION,
            DBSForeignKeyModifyRule.NO_ACTION,
            false);

        return new UITask<SQLServerTableForeignKey>() {
            @Override
            protected SQLServerTableForeignKey runTask() {
                EditForeignKeyPage editPage = new EditForeignKeyPage(
                    "Create foreign key",
                    foreignKey,
                    new DBSForeignKeyModifyRule[] {
                        DBSForeignKeyModifyRule.NO_ACTION,
                        DBSForeignKeyModifyRule.CASCADE, DBSForeignKeyModifyRule.RESTRICT,
                        DBSForeignKeyModifyRule.SET_NULL,
                        DBSForeignKeyModifyRule.SET_DEFAULT });
                if (!editPage.edit()) {
                    return null;
                }

                foreignKey.setReferencedKey(editPage.getUniqueConstraint());
                foreignKey.setName(getNewConstraintName(monitor, foreignKey));
                foreignKey.setDeleteRule(editPage.getOnDeleteRule());
                foreignKey.setUpdateRule(editPage.getOnUpdateRule());
                int colIndex = 1;
                for (EditForeignKeyPage.FKColumnInfo tableColumn : editPage.getColumns()) {
                    foreignKey.addColumn(
                        new SQLServerTableForeignKeyColumn(
                            foreignKey,
                            (SQLServerTableColumn) tableColumn.getOwnColumn(),
                            colIndex++,
                            (SQLServerTableColumn) tableColumn.getRefColumn()));
                }
                return foreignKey;
            }
        }.execute();
    }

}
