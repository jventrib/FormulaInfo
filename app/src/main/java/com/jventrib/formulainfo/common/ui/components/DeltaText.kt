package com.jventrib.formulainfo.common.ui.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DeltaText(
    delta: Int,
    modifier: Modifier = Modifier,
    content: (String) -> String = {"$it"}
) {
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        val text =
            if (delta > 0) "+$delta" else if (delta < 0) "$delta" else "-"
        val wholeText = content(text)
        append(wholeText)
        addStyle(
            SpanStyle(if (delta > 0) Color.Red else if (delta < 0) Color.Green else Color.Gray),
            0,
            wholeText.length
        )
    }

    Text(
        text = annotatedLinkString,
        modifier = modifier,
    )
}


@Composable
fun DeltaTextP(delta: Int, modifier: Modifier = Modifier) {
    DeltaText(delta = delta, modifier = modifier) {"($it)"}
}
@Preview
@Composable
fun DeltaTextPreview() {
    DeltaText(2) {"($it)"}
}

@Preview
@Composable
fun DeltaTextPreviewNegative() {
    DeltaTextP(-2)
}

@Preview
@Composable
fun DeltaTextPreviewZero() {
    DeltaTextP(0)
}
