package com.example.mansshop_boot.domain.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification")
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "userId")
	private Member member;

	@Column(length = 50, 
			nullable = false
	)
	private String type;

	@Column(length = 255, 
			nullable = false
	)
	private String title;

	private Long relatedId;

	@Column(nullable = false,
			columnDefinition = "TINYINT(1) DEFAULT 0"
	)
	private boolean isRead;

	@CreationTimestamp
	@Column(nullable = false, columnDefinition = "DATETIME(3) DEFAULT CURRENT_TIMESTAMP(3)")
	private LocalDateTime createdAt;

}
