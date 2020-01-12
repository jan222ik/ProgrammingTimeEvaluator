package com.github.jan222ik.programming.time.evaluator.ui.views

import com.github.jan222ik.programming.time.evaluator.data.Parser
import com.github.jan222ik.programming.time.evaluator.data.WeekWrapper
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.collections.FXCollections
import javafx.scene.layout.Pane
import tornadofx.*

class AllWeeklyTableView : View("Week Overview") {
    override val root = Pane()

    init {
        with(root) {
            vbox {
                label("Tableview from a map")
                tableview(FXCollections.observableArrayList<WeekWrapper>(Parser.fromMailFolder())) {
                    readonlyColumn("From", WeekWrapper::startDate)
                    readonlyColumn("Until", WeekWrapper::endDate)
                    column<WeekWrapper, String>("Minutes") { col -> SimpleStringProperty(col.value.total.minutes.toString()) }
                    column<WeekWrapper, Int>("Projects") { col -> SimpleIntegerProperty(col.value.projects.size) as ObservableValue<Int> }
                    column<WeekWrapper, Int>("Languages") { col -> SimpleIntegerProperty(col.value.languages.size) as ObservableValue<Int> }
                    column<WeekWrapper, Int>("IDEs") { col -> SimpleIntegerProperty(col.value.ides.size) as ObservableValue<Int> }
                    column<WeekWrapper, Int>("OSs") { col -> SimpleIntegerProperty(col.value.oss.size) as ObservableValue<Int> }
                    column<WeekWrapper, Int>("Machines") { col -> SimpleIntegerProperty(col.value.machines.size) as ObservableValue<Int> }
                    resizeColumnsToFitContent()
                }
            }
        }
    }
}
