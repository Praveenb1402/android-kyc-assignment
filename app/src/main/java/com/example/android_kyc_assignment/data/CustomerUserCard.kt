package com.example.android_kyc_assignment.data

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun CustomerCard(
    customer: CustomerDatamodel,
    onClick: () -> Unit = {},
    onKycClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(250.dp),
        shape = RoundedCornerShape(12.dp),

        onClick = onClick
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                AsyncImage(
                    model = if (customer.verified && customer.selfiePath != null)
                        customer.selfiePath
                    else
                        customer.image,
                    contentDescription = customer.name,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAEAEA)),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    color = if (customer.verified) Color(0xFF05EA38) else Color(0xFFFFE7C2),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        text = if (customer.verified) "VERIFIED" else "PENDING",
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 3.dp
                        ),
                        fontSize = 10.sp,
                        color = Color(0xFFE58E00)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = customer.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "•••• ${customer.accountNumber.takeLast(4)}",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "₹${customer.balance.toLong()}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = if (customer.verified) onClick else onKycClick,
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text(
                    text = if (customer.verified) "View" else "Do KYC",
                    fontSize = 11.sp
                )
            }
        }
    }
}