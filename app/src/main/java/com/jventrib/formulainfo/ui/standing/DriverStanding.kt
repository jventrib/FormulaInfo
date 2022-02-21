package com.jventrib.formulainfo.ui.standing

import android.graphics.Rect
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jventrib.formulainfo.model.aggregate.DriverStanding
import com.jventrib.formulainfo.model.db.Constructor
import com.jventrib.formulainfo.model.db.Driver
import com.jventrib.formulainfo.model.db.Result
import com.jventrib.formulainfo.model.db.ResultInfo
import com.jventrib.formulainfo.ui.common.composable.Image
import com.jventrib.formulainfo.ui.common.composable.ItemCard
import com.jventrib.formulainfo.ui.common.formatDecimal

@Composable
fun DriverStanding(
    driverStanding: DriverStanding,
    onDriverSelected: (driver: Driver) -> Any
) {
    ItemCard(
        image = {
            Image(
                imageModel = driverStanding.driver.image,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillWidth,
                faceBox = Rect.unflattenFromString(driverStanding.driver.faceBox)
            )
        },
        onItemSelected = { onDriverSelected(driverStanding.driver) }
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = "${driverStanding.position}:${driverStanding.driver.givenName} " +
                        driverStanding.driver.familyName,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = driverStanding.constructor.name,
                    style = MaterialTheme.typography.body1
                )
            }
            Column(horizontalAlignment = End, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${driverStanding.points.formatDecimal()} pts",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(End)
                )
            }
        }
    }
}

@Preview
@Composable
fun DriverResultPreview() {
    val resultSample = getResultSample("Verstappen", 1)
    DriverStanding(
        driverStanding = DriverStanding(
            resultSample.driver,
            resultSample.constructor,
            46f,
            1,
            1
        )
    ) {
    }
}

fun getResultSample(driver: String, position: Int) = Result(
    ResultInfo(
        "11",
        2021,
        2,
        33,
        position,
        "1",
        25.0f,
        driver,
        "RedBull",
        2,
        70,
        "Finished",
        ResultInfo.Time(111, "111"),

        ResultInfo.FastestLap(
            position,
            position,
            ResultInfo.Time(111, "111"),
            ResultInfo.FastestLap.AverageSpeed("Kph", 170.0f)
        )
    ),
    Driver(
        driver,
        33,
        driver.uppercase().substring(0, 3),
        "url",
        "John",
        driver,
        "1999",
        "NL",
        "img",
        null,
        1
    ),
    Constructor("RedBull", "url", "RedBull", "UK", "img")
)
