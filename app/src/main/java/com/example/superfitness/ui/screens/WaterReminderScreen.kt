package com.example.superfitness.ui.screens

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.superfitness.data.local.db.entity.WaterIntake
import com.example.superfitness.viewmodel.WaterIntakeViewModel
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackingApp(viewModel: WaterIntakeViewModel) {
    val waterRecords by viewModel.intakesByDate.collectAsState(initial = emptyList())
    val dailyTotal by viewModel.dailyTotal.collectAsState()
    val targetAmount = 2000 // 2L = 2000ml

    var showReminderDialog by remember { mutableStateOf(false) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var reminderInterval by remember { mutableStateOf(60) } // phút

    val context = LocalContext.current
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showNotification(context, "Đã đến giờ uống nước!", "Bạn đã uống đủ nước chưa?")
        }
    }

    // Xử lý nhắc nhở định kỳ
    LaunchedEffect(reminderEnabled, reminderInterval) {
        if (reminderEnabled) {
            while (true) {
                delay(TimeUnit.MINUTES.toMillis(reminderInterval.toLong()))
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    showNotification(context, "Đã đến giờ uống nước!", "Bạn đã uống đủ nước chưa?")
                }
            }
        }
    }

    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Theo dõi nước uống") },
                    actions = {
                        IconButton(onClick = { showReminderDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Nhắc nhở"
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White) // ⬅️ Đổi màu nền thành trắng
                    .padding(padding)
            ) {
                WaterHeader(
                    currentAmount = dailyTotal / 1000f, // Chuyển từ ml sang L
                    targetAmount = targetAmount / 1000f
                )

                WaterContent(
                    currentAmount = dailyTotal / 1000f,
                    targetAmount = targetAmount / 1000f,
                    waterRecords = waterRecords,
                    viewModel = viewModel,

                    onAddWater = { amount, type ->
                        viewModel.addIntake(amount, type)
                    }
                )
            }
        }

        if (showReminderDialog) {
            ReminderSettingsDialog(
                enabled = reminderEnabled,
                interval = reminderInterval,
                onEnabledChange = { reminderEnabled = it },
                onIntervalChange = { reminderInterval = it },
                onDismiss = { showReminderDialog = false }
            )
        }
    }
}

@Composable
fun ReminderSettingsDialog(
    enabled: Boolean,
    interval: Int,
    onEnabledChange: (Boolean) -> Unit,
    onIntervalChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val intervalOptions = listOf(1,30, 60, 90, 120) // phút

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cài đặt nhắc nhở") },

        text = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Bật nhắc nhở")
                    Switch(
                        checked = enabled,
                        onCheckedChange = onEnabledChange
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (enabled) {
                    Text("Khoảng thời gian nhắc nhở (phút)", modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        intervalOptions.forEach { option ->
                            FilterChip(
                                selected = interval == option,
                                onClick = { onIntervalChange(option) },
                                label = { Text("$option") }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Xong")
            }
        }
    )
}

@Composable
fun WaterHeader(
    currentAmount: Float,
    targetAmount: Float
) {
    val progress = currentAmount / targetAmount
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)) // Gradient nhạt
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Xin chào!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333) // Màu chữ đậm
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hôm nay bạn đã uống nước chưa",
                fontSize = 16.sp,
                color = Color(0xFF333333) // Màu chữ đậm
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Circular progress indicator
            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    strokeWidth = 10.dp
                )

                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF2196F3), // Màu xanh cho progress
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = Color(0xFF333333), // Màu chữ đậm
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%.1fL / %.1fL".format(currentAmount, targetAmount),
                        color = Color(0xFF333333), // Màu chữ đậm
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun WaterContent(
    currentAmount: Float,
    targetAmount: Float,
    waterRecords: List<WaterIntake>,
    viewModel: WaterIntakeViewModel,
    onAddWater: (amount: Int, type: String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedRecord by remember { mutableStateOf<WaterIntake?>(null) }

    // Thêm dialog chỉnh sửa
    if (showEditDialog) {
        selectedRecord?.let { record ->
            EditWaterDialog(
                record = record,
                onDismiss = { showEditDialog = false },
                onConfirm = { id, amount, type, time  ->
                    viewModel.updateIntake(id, amount, type, time)
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Chỉ gọi TodayDrinkList 1 lần
        TodayDrinkList(
            waterRecords = waterRecords,
            onEdit = { record ->
                selectedRecord = record
                showEditDialog = true
            }
        )

        // Floating action button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp) ,// ⬅️ Thêm khoảng cách với mép

            containerColor = Color(0xFF2196F3)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Thêm nước",
                tint = Color.White
            )
        }
    }

    if (showAddDialog) {
        AddWaterDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { amount, type ->
                onAddWater(amount, type)
                showAddDialog = false
            }
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddWaterDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Int, type: String) -> Unit
) {
    var amount by remember { mutableStateOf(200) }
    var type by remember { mutableStateOf("Nước") }
    val amountOptions = listOf(100,200, 300, 400 )
    val typeOptions = listOf("Nước", "Cà phê", "Trà" )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm đồ uống") },
        text = {
            Column {
                Text("Lượng nước (ml)", modifier = Modifier.padding(bottom = 4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    amountOptions.forEach { option ->
                        FilterChip(
                            selected = amount == option,
                            onClick = { amount = option },
                            label = { Text("$option ml") }
                        )
                    }
                }

                Text("Loại đồ uống", modifier = Modifier.padding(bottom = 4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    typeOptions.forEach { option ->
                        FilterChip(
                            selected = type == option,
                            onClick = { type = option },
                            label = { Text(option) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(amount, type) }) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ")
            }
        }
    )
}

@Composable
fun TodayDrinkList(
    waterRecords: List<WaterIntake>,
    onEdit: (WaterIntake) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đồ uống hôm nay đã được thêm vào",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF333333) // Màu chữ đậm
            )

            TextButton(onClick = { /* Handle edit all */ }) {
                Text(
                    text = "Chỉnh sửa",
                    color = Color(0xFF64B5F6)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 80.dp) // ⬅️ Thêm padding tránh FAB
        )        {
            items(waterRecords) { record ->
                DrinkItem(
                    record = record,
                    onEdit = { onEdit(record) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
@Composable
fun getDrinkIcon(type: String): ImageVector {
    return when (type) {
        "Cà phê" -> Icons.Default.Coffee
        "Trà" -> Icons.Default.LocalCafe
        else -> Icons.Default.LocalDrink
    }
}


@Composable
fun DrinkItem(
    record: WaterIntake,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)  //Color(0xFF1E1E1E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f) // ⬅️ Chiếm không gian còn lại
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Icon(
                        imageVector = getDrinkIcon(record.type),
                        contentDescription = record.type,
                        tint = Color(0xFF2196F3), // Màu icon xanh
                        modifier = Modifier.size(24.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${record.amount} ml",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF64B5F6)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = record.type,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = record.time,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Chỉnh sửa",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditWaterDialog(
    record: WaterIntake,
    onDismiss: () -> Unit,
    onConfirm: (id: Int, amount: Int, type: String, time: String) -> Unit
) {
    var amount by remember { mutableStateOf(record.amount) }
    var type by remember { mutableStateOf(record.type) }
    val amountOptions = listOf(100, 200, 300, 400)
    val typeOptions = listOf("Nước", "Cà phê", "Trà")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa đồ uống") },
        text = {
            Column {
                // Phần chọn lượng nước
                Text("Lượng nước (ml)", modifier = Modifier.padding(bottom = 4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    amountOptions.forEach { option ->
                        FilterChip(
                            selected = amount == option,
                            onClick = { amount = option },
                            label = { Text("$option ml") }
                        )
                    }
                }

                // Phần chọn loại đồ uống
                Text("Loại đồ uống", modifier = Modifier.padding(bottom = 4.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    typeOptions.forEach { option ->
                        FilterChip(
                            selected = type == option,
                            onClick = { type = option },
                            label = { Text(option) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(record.id.toInt(), amount, type, record.time)
                onDismiss()
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ")
            }
        }
    )
}
data class WaterRecord(
    val amount: Int,
    val type: String,
    val time: String
)

// Hàm tạo notification channel
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "water_reminder_channel",
            "Nhắc nhở uống nước",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Nhắc nhở bạn uống nước định kỳ"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

// Hàm hiển thị notification
// Hàm hiển thị notification đã sửa
fun showNotification(context: Context, title: String, message: String) {
    try {
        val builder = NotificationCompat.Builder(context, "water_reminder_channel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        NotificationManagerCompat.from(context).notify(1, builder.build())
    } catch (e: Exception) {
        Log.e("Notification", "Lỗi hiển thị thông báo: ${e.message}")
    }
}

