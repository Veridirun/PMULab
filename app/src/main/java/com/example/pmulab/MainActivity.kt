package com.example.pmulab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.example.pmulab.ui.theme.PMULabTheme

data class AdvertisementItem(
    val id: Int,
    val title: String,
    var likes: Int = 0
)

class AdvertisementViewModel : ViewModel(){
    private val advertisements = mutableStateListOf(
        AdvertisementItem(1, "Университеты России переходят на гибридное обучение: преимущества и вызовы для студентов"),
        AdvertisementItem(2, "Учёные университета разработали инновационный метод лечения редких генетических заболеваний"),
        AdvertisementItem(3, "Студенты протестуют против повышения стоимости обучения в ведущих вузах страны"),
        AdvertisementItem(4, "Новый кампус университета открыт: как изменятся условия жизни студентов?"),
        AdvertisementItem(5, "Рейтинг лучших университетов мира 2024: российские вузы поднимаются в списке"),
        AdvertisementItem(6, "Университеты увеличивают грантовую поддержку для студентов: новые программы и условия"),
        AdvertisementItem(7, "Студенты представили уникальные стартапы на ежегодной конференции инноваций в университете"),
        AdvertisementItem(8, "Исследователи университета раскрыли тайны древних цивилизаций с помощью новейших технологий"),
        AdvertisementItem(9, "Цифровизация образования: как университеты адаптируются к новым технологиям обучения"),
        AdvertisementItem(10, "Университеты объединяются с индустрией: перспективы стажировок и трудоустройства студентов")
    )
    var displayedAdvertisements by mutableStateOf(listOf<AdvertisementItem>())

    init {
        displayedAdvertisements = advertisements.take(4)
    }

    fun likeAdvertisement(advertisement: AdvertisementItem) {
        val updatedList = displayedAdvertisements.map {
            if (it.id == advertisement.id) {
                it.copy(likes = it.likes + 1)
            } else {
                it
            }
        }
        displayedAdvertisements = updatedList
    }

    fun updateRandomNews() {
        val unusedAdvertisements = advertisements - displayedAdvertisements.toSet()
        val randomIndex = Random.nextInt(displayedAdvertisements.size)
        val randomAdvertisements = unusedAdvertisements.random()
        displayedAdvertisements = displayedAdvertisements.toMutableList().also {
            it[randomIndex] = randomAdvertisements
        }
    }
}

@Composable
private fun Advertisement(modifier: Modifier = Modifier, advertisement: AdvertisementItem, onLike: (AdvertisementItem) -> Unit) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.weight(9f),
                    text = "${advertisement.id}. ${advertisement.title}",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
                Box(
                    modifier = Modifier
                        .background(Color.Cyan)
                        .clickable { onLike(advertisement) }
                        .padding(8.dp)
                        .weight(1f),
                        contentAlignment = Alignment.Center

                ) {
                    Text(
                        text = "Likes: ${advertisement.likes}",
                        color = Color.White
                    )
                }
            }
        }
    }
}


    @Composable
    private fun AdvertisementWindow(viewModel: AdvertisementViewModel = viewModel()) {
        LaunchedEffect(Unit) {
            while (true) {
                delay(5000)
                viewModel.updateRandomNews()
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            Row(Modifier.weight(1f)) {
                Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[0], onLike = { viewModel.likeAdvertisement(it) })
                Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[1], onLike = { viewModel.likeAdvertisement(it) })
            }
            Row(Modifier.weight(1f)) {
                Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[2], onLike = { viewModel.likeAdvertisement(it) })
                Advertisement(modifier = Modifier.weight(1f), advertisement = viewModel.displayedAdvertisements[3], onLike = { viewModel.likeAdvertisement(it) })
            }
        }
    }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdvertisementWindow()
        }
    }
}

