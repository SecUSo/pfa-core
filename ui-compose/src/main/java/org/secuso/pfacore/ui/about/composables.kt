package org.secuso.pfacore.ui.about

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import org.secuso.pfacore.R
import org.secuso.pfacore.model.about.About
import org.secuso.pfacore.ui.composables.CenterLines
import org.secuso.pfacore.ui.composables.CenterText
import org.secuso.pfacore.ui.theme.PrivacyFriendlyCoreTheme

private class AboutPreviewProvider : PreviewParameterProvider<About> {
    override val values = listOf(
        About(
            name = "Privacy Friendly Core",
            version = "1.0.0",
            authors = "Patrick Schneider",
            repo = "https://github.com/secuso/privacy-friendly-core"
        )
    ).asSequence()
}

@Composable
fun Authors(authors: String) {
    CenterLines(
        sentences = listOf(
            stringResource(id = R.string.about_author),
            "$authors ${stringResource(id = R.string.about_author_contributors)}\n"
        ), modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )
}

@Composable
fun PfaLogo() {
    Image(painter = painterResource(id = R.drawable.privacyfriendlyappslogo), contentDescription = "Privacy Friendly Apps", modifier = Modifier.fillMaxWidth())
}

@Composable
fun SecusoLogo() {
    CenterText(text = stringResource(id = R.string.about_affiliation), style = TextStyle(fontWeight = FontWeight.Bold))
    Image(
        painter = painterResource(id = R.drawable.secuso_logo_blau_blau),
        contentDescription = "SECUSO - Security, Usability and Society",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun Header(name: String, version: String) {
    CenterLines(
        sentences = listOf(
            name,
            stringResource(id = R.string.about_version_number) + " v$version"
        )
    )
}

@Composable
fun Footer(repo: String) {
    val links = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            pushStringAnnotation(
                tag = "URL", annotation = "https://secuso.org"
            )
            append(stringResource(id = R.string.about_url))
        }
        append(System.lineSeparator())
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
        ) {
            pushStringAnnotation(
                tag = "URL", annotation = repo
            )
            append(stringResource(id = R.string.about_github))
        }
    }
    val uriHandler = LocalUriHandler.current

    Column(Modifier.fillMaxWidth()) {
        CenterLines(
            sentences = listOf(stringResource(id = R.string.about_privacy_friendly), stringResource(id = R.string.about_more_info)),
            style = MaterialTheme.typography.bodyMedium

        )
        ClickableText(
            text = links,
            style = MaterialTheme.typography.bodyMedium.plus(TextStyle(textAlign = TextAlign.Center)),
            modifier = Modifier.fillMaxWidth(),
            onClick = { offset ->
                links.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.let { url ->
                    uriHandler.openUri(url.item)
                }
            })
    }
}

@Composable
fun AboutVertical(data: About) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(PaddingValues(top = 8.dp)),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        PfaLogo()
        Header(name = data.name, version = data.version)
        Authors(authors = data.authors)
        SecusoLogo()
        Footer(repo = data.repo)
    }
}

@Composable
fun AboutLandscape(data: About) {
    Column(Modifier.fillMaxWidth()) {
        PfaLogo()
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.fillMaxWidth()) {
                Header(name = data.name, version = data.version)
                Authors(authors = data.authors)
                SecusoLogo()
            }
            Column(Modifier.fillMaxWidth()) {
                Footer(repo = data.repo)
            }
        }
    }
}

@Composable
fun AboutOrientation(data: About) {
    when (LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> AboutLandscape(data = data)
        else -> AboutVertical(data = data)
    }
}

@Preview
@Composable
fun About(@PreviewParameter(AboutPreviewProvider::class) data: About) {
    AboutOrientation(data = data)
}

@Preview
@Composable
fun AboutPreview() {
    PrivacyFriendlyCoreTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), color = MaterialTheme.colorScheme.background
        ) {
            About(data = AboutPreviewProvider().values.first())
        }
    }
}