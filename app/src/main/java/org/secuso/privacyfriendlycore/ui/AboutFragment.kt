package org.secuso.privacyfriendlycore.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.ViewCompositionStrategy
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
import androidx.fragment.app.Fragment
import org.secuso.privacyfriendlycore.R
import org.secuso.privacyfriendlycore.ui.composables.CenterLines
import org.secuso.privacyfriendlycore.ui.composables.CenterText
import org.secuso.privacyfriendlycore.ui.theme.PrivacyFriendlyCoreTheme

data class AboutData(
    val name: String,
    val version: String,
    val authors: String,
    val repo: String
)
private class AboutPreviewProvider : PreviewParameterProvider<AboutData> {
    override val values = listOf(AboutData(
        name = "Privacy Friendly Core",
        version = "1.0.0",
        authors = "Patrick Schneider",
        repo = "https://github.com/secuso/privacy-friendly-core"
    )).asSequence()
}

class AboutFragment(
    private val name: String,
    private val version: String,
    private val authors: String,
    private val repo: String
): Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent { 
                About(data = AboutData(name = name, version = version, authors = authors, repo = repo))
            }
        }
        
        return view
    }
}

@Composable
fun Authors(authors: String) {
    CenterLines(
        sentences = listOf(
        stringResource(id = R.string.about_author),
        authors,
        stringResource(id = R.string.about_author_contributors)
    ), modifier = Modifier.padding(top = 2.dp, bottom = 2.dp))
}

@Composable
fun PfaLogo() {
    Image(painter = painterResource(id = R.drawable.privacyfriendlyappslogo), contentDescription = "Privacy Friendly Apps", modifier = Modifier.fillMaxWidth(),)
}

@Composable
fun SecusoLogo() {
    CenterText(text = stringResource(id = R.string.about_affiliation))
    Image(painter = painterResource(id = R.drawable.secuso_logo_blau_blau), contentDescription = "SECUSO - Security, Usability and Society")
}

@Composable
fun Header(name: String, version: String) {
    CenterLines(
        sentences = listOf(
        name,
        stringResource(id = R.string.about_version_number) + " v$version"
    ))
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
            append(System.lineSeparator())
            pushStringAnnotation(
                tag = "URL", annotation = repo
            )
            append(stringResource(id = R.string.about_github))
        }
    }
    val uriHandler = LocalUriHandler.current

    Column(Modifier.fillMaxWidth()) {
        CenterLines(
            sentences = listOf(stringResource(id = R.string.about_privacy_friendly), stringResource(id = R.string.about_more_info))
        )
        ClickableText(
            text = links,
            style = TextStyle(textAlign = TextAlign.Center),
            onClick = { offset ->
                links.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.let { url ->
                    uriHandler.openUri(url.item)
                }
            })
    }
}

@Composable
fun AboutVertical(data: AboutData) {
    Column(Modifier.fillMaxWidth().padding(PaddingValues(top = 8.dp))) {
        PfaLogo()
        Header(name = data.name, version = data.version)
        Authors(authors = data.authors)
        SecusoLogo()
        Footer(repo = data.repo)
    }
}

@Composable
fun AboutLandscape(data: AboutData) {
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
fun AboutOrientation(data: AboutData) {
    when(LocalConfiguration.current.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> AboutLandscape(data = data)
        else -> AboutVertical(data = data)
    }
}

@Preview
@Composable
fun About(@PreviewParameter(AboutPreviewProvider::class) data: AboutData) {
    PrivacyFriendlyCoreTheme {
        Surface(modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()), color = MaterialTheme.colorScheme.background) {
            AboutOrientation(data = data)
        }
    }
}

@Preview
@Composable
fun AboutPreview() {
    About(data = AboutPreviewProvider().values.first())
}