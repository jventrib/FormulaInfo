package com.jventrib.formulainfo.ui.common.composable

import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun LinkText(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable LinksScope.() -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        append(text)
        LinksScope(this, text).content()
    }

    ClickableText(
        text = annotatedLinkString,
        modifier = modifier,
        onClick = {
            annotatedLinkString
                .getStringAnnotations("URL", it, it)
                .firstOrNull()?.let { stringAnnotation ->
                    uriHandler.openUri(stringAnnotation.item)
                }
        }
    )
}

class LinksScope(internal val builder: AnnotatedString.Builder, val text: String)

@Composable
fun LinksScope.Link(linkText: String, linkUrl: String) {
    val startIndex = text.indexOf(string = linkText)
    val endIndex = startIndex + linkText.length
    builder.addStyle(
        style = SpanStyle(
            color = Color(0xff64B5F6),
//                fontSize = 18.sp,
            textDecoration = TextDecoration.Underline
        ), start = startIndex, end = endIndex
    )
    builder.addStringAnnotation(
        tag = "URL",
        annotation = linkUrl,
        start = startIndex,
        end = endIndex
    )
}
