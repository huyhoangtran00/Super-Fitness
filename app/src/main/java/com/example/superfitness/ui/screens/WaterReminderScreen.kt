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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterTrackingApp() {
    var currentAmount by remember { mutableStateOf(0.6f) }
    val targetAmount = 2f
    val waterRecords = remember { mutableStateListOf(
        WaterRecord(200, "Nước", "15:10"),
        WaterRecord(200, "Nước", "15:10")
    )}
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

    MaterialTheme(colorScheme = darkColorScheme()) {
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
                    .background(Color(0xFF121212))
                    .padding(padding)
            ) {
                WaterHeader(
                    currentAmount = currentAmount,
                    targetAmount = targetAmount
                )

                WaterContent(
                    currentAmount = currentAmount,
                    targetAmount = targetAmount,
                    waterRecords = waterRecords,
                    onAddWater = { amount, type ->
                        val newRecord = WaterRecord(
                            amount = amount,
                            type = type,
                            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                        )
                        waterRecords.add(0, newRecord)
                        currentAmount = (currentAmount + amount / 1000f).coerceAtMost(targetAmount)
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
                    colors = listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
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
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hôm nay bạn đã uống nước chưa",
                fontSize = 16.sp,
                color = Color.White
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
                    color = Color.White.copy(alpha = 0.2f),
                    strokeWidth = 10.dp
                )

                CircularProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White,
                    strokeWidth = 10.dp,
                    strokeCap = StrokeCap.Round
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%.1fL / %.1fL".format(currentAmount, targetAmount),
                        color = Color.White,
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
    waterRecords: List<WaterRecord>,
    onAddWater: (amount: Int, type: String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TodayDrinkList(
                waterRecords = waterRecords,
                onEdit = { /* Handle edit */ }
            )
        }

        // Floating action button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
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

@Composable
fun AddWaterDialog(
    onDismiss: () -> Unit,
    onConfirm: (amount: Int, type: String) -> Unit
) {
    var amount by remember { mutableStateOf(200) }
    var type by remember { mutableStateOf("Nước") }
    val amountOptions = listOf(100,200, 300, 400,500 )
    val typeOptions = listOf("Nước", "Cà phê", "Trà" )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm đồ uống") },
        text = {
            Column {
                Text("Lượng nước (ml)", modifier = Modifier.padding(bottom = 4.dp))
                Row(
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
    waterRecords: List<WaterRecord>,
    onEdit: (WaterRecord) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Đồ uống hôm nay đã được thêm vào",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            TextButton(onClick = { /* Handle edit all */ }) {
                Text(
                    text = "Chỉnh sửa",
                    color = Color(0xFF64B5F6)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
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
fun DrinkItem(
    record: WaterRecord,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm",
                        tint = Color(0xFF64B5F6),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "${record.amount} ml",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
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


@Preview
@Composable
fun PreviewWaterTrackingApp() {
    WaterTrackingApp()
}