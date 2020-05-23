package com.tolstun.i18n.database.table

import slick.ast.Select
import slick.ast.ColumnOption.PrimaryKey
import slick.jdbc.MySQLProfile.api._
import slick.lifted.{ProvenShape, Tag}


abstract class ExtendedTable[E](tableTag: Tag,
                                tableSchema: Option[String],
                                tableName: String) extends Table[E](tableTag, tableSchema, tableName) {

  def columns: Product


  implicit class RepAnyRepBehaviour(val rep: Rep[_]) extends AnyRef {
    def getColumnName = rep.toNode.asInstanceOf[Select].field.name
  }


  def findColumn[E](columnName: String): Option[Rep[E]] = {
    columns.productIterator
      .map(_.asInstanceOf[Rep[_]])
      .toSeq.find(n => n.getColumnName == columnName)
      .map(_.asInstanceOf[Rep[E]])
  }

}
