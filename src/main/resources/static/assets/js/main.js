// main.js
(function($) {
	"use strict";

	/*----------------------------
	Responsive menu Active
	------------------------------ */
	$(".mainmenu ul#primary-menu").slicknav({
		allowParentLinks: true,
		prependTo: '.responsive-menu',
	});

	/*----------------------------
	START - Schedule Calendar
	------------------------------ */
	function initializeScheduleButtons() {
		const scheduleHeader = document.getElementById("scheduleHeader");
		if (!scheduleHeader) return;

		const today = new Date();
		const daysToShow = 7;
		scheduleHeader.innerHTML = '';

		const urlParams = new URLSearchParams(window.location.search);
		const selectedDateFromUrl = urlParams.get("date") || `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

		for (let i = 0; i < daysToShow; i++) {
			const date = new Date(today);
			date.setDate(today.getDate() + i);
			const dateStr = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
			const dayOfWeek = date.toLocaleDateString('vi-VN', { weekday: 'short' });

			const button = document.createElement("button");
			button.classList.add("date-btn");
			button.dataset.date = dateStr;
			button.innerHTML = `<span class="day">${date.getDate()}</span><span class="weekday">${dayOfWeek.toUpperCase()}</span>`;
			if (i === 0) button.classList.add("today");
			if (dateStr === selectedDateFromUrl) button.classList.add("active");

			button.addEventListener("click", () => {
				document.querySelectorAll(".date-btn").forEach(btn => btn.classList.remove("active"));
				button.classList.add("active");
				document.getElementById("selectedDate").value = dateStr;
				fetchShowtimes();
			});

			scheduleHeader.appendChild(button);
		}
	}

	/*----------------------------
	START - Cinema Tabs
	------------------------------ */
	function initializeCinemaTabs() {
		const urlParams = new URLSearchParams(window.location.search);
		const selectedBrandFromUrl = urlParams.get("brandName") || "Tất cả";

		document.querySelectorAll(".cinema-tabs .tab-btn").forEach(tab => {
			const brandName = tab.dataset.name;
			if (brandName === selectedBrandFromUrl) tab.classList.add("active");

			tab.addEventListener("click", function(event) {
				event.preventDefault();
				if (!brandName) {
					console.error("Cinema name not found!");
					return;
				}

				document.querySelectorAll(".cinema-tabs .tab-btn").forEach(t => t.classList.remove("active"));
				this.classList.add("active");
				document.getElementById("selectedBrand").value = brandName;
				fetchShowtimes();
			});
		});
	}

	/*----------------------------
		START - Fetch Showtimes via AJAX
		------------------------------ */
	function fetchShowtimes() {
		const form = document.getElementById("showtimeForm");
		const loading = document.getElementById("showtimeLoading");
		loading.style.display = "block";

		const formData = new FormData(form);
		const url = `${form.action}?${new URLSearchParams(formData).toString()}`;

		fetch(url, { method: 'GET' })
			.then(response => response.text())
			.then(html => {
				const parser = new DOMParser();
				const doc = parser.parseFromString(html, 'text/html');
				const newCinemaInfo = doc.querySelector('#cinemaInfo').innerHTML;
				document.getElementById("cinemaInfo").innerHTML = newCinemaInfo;
			})
			.catch(error => console.error("Error fetching showtimes:", error))
			.finally(() => loading.style.display = "none");

		// Cập nhật URL trình duyệt mà không tải lại trang
		window.history.pushState({}, document.title, url);
	}

	/*----------------------------
	START - Reset Showtimes
	------------------------------ */
	document.getElementById("resetShowtimes")?.addEventListener("click", function() {
		document.getElementById("selectedDate").value = "";
		document.getElementById("selectedBrand").value = "Tất cả";
		document.querySelectorAll(".date-btn").forEach(btn => btn.classList.remove("active"));
		document.querySelectorAll(".cinema-tabs .tab-btn").forEach(tab => tab.classList.remove("active"));
		document.querySelector(".cinema-tabs .tab-btn[data-name='Tất cả']").classList.add("active");
		document.querySelector(".date-btn.today").classList.add("active");
		fetchShowtimes();
	});

	// Khởi tạo khi trang tải
	document.addEventListener("DOMContentLoaded", function() {
		initializeScheduleButtons();
		initializeCinemaTabs();
	});

	/*----------------------------
	START - Scroll to Top
	------------------------------ */
	$(window).on('scroll', function() {
		if ($(this).scrollTop() > 600) {
			$('.scrollToTop').fadeIn();
		} else {
			$('.scrollToTop').fadeOut();
		}
	});
	$('.scrollToTop').on('click', function() {
		$('html, body').animate({ scrollTop: 0 }, 2000);
		return false;
	});

	/*----------------------------
	START - Buy Ticket Popup
	------------------------------ */
	$('.menu-area ul > li > .theme-btn').on('click', function() {
		$('.buy-ticket').show();
		return false;
	});
	$('.buy-ticket .buy-ticket-area > a').on('click', function() {
		$('.buy-ticket').hide();
		return false;
	});

	/*----------------------------
	START - Login Popup
	------------------------------ */
	$('.login-popup').on('click', function() {
		$('.login-area').show();
		return false;
	});
	$('.login-box > a').on('click', function() {
		$('.login-area').hide();
		return false;
	});

	/*----------------------------
	START - Slider Activation
	------------------------------ */
	var heroSlider = $('.hero-area-slider');
	heroSlider.owlCarousel({
		loop: true,
		dots: true,
		autoplay: false,
		autoplayTimeout: 4000,
		nav: false,
		items: 1,
		responsive: {
			992: {
				dots: false,
			}
		}
	});
	heroSlider.on('changed.owl.carousel', function(property) {
		var current = property.item.index;
		var prevRating = $(property.target).find(".owl-item").eq(current).prev().find('.hero-area-slide').html();
		var nextRating = $(property.target).find(".owl-item").eq(current).next().find('.hero-area-slide').html();
		$('.thumb-prev .hero-area-slide').html(prevRating);
		$('.thumb-next .hero-area-slide').html(nextRating);
	});
	$('.thumb-next').on('click', function() {
		heroSlider.trigger('next.owl.carousel', [300]);
		return false;
	});
	$('.thumb-prev').on('click', function() {
		heroSlider.trigger('prev.owl.carousel', [300]);
		return false;
	});

	/*----------------------------
	START - News Slider
	------------------------------ */
	var newsSlider = $('.news-slider');
	newsSlider.owlCarousel({
		loop: true,
		dots: true,
		autoplay: false,
		autoplayTimeout: 4000,
		nav: false,
		items: 1,
		responsive: {
			992: {
				dots: false,
			}
		}
	});
	newsSlider.on('changed.owl.carousel', function(property) {
		var current = property.item.index;
		var prevRating = $(property.target).find(".owl-item").eq(current).prev().find('.single-news').html();
		var nextRating = $(property.target).find(".owl-item").eq(current).next().find('.single-news').html();
		$('.news-prev .single-news').html(prevRating);
		$('.news-next .single-news').html(nextRating);
	});
	$('.news-next').on('click', function() {
		newsSlider.trigger('next.owl.carousel', [300]);
		return false;
	});
	$('.news-prev').on('click', function() {
		newsSlider.trigger('prev.owl.carousel', [300]);
		return false;
	});

	/*----------------------------
	START - Video Slider
	------------------------------ */
	var videoSlider = $('.video-slider');
	videoSlider.owlCarousel({
		loop: true,
		dots: true,
		autoplay: false,
		autoplayTimeout: 4000,
		nav: false,
		responsive: {
			0: {
				items: 1,
				margin: 0
			},
			576: {
				items: 2,
				margin: 30
			},
			768: {
				items: 3,
				margin: 30
			},
			992: {
				items: 4,
				margin: 30
			}
		}
	});

	/*----------------------------
	START - Videos Popup
	------------------------------ */
	$('.popup-youtube').magnificPopup({ type: 'iframe' });
	$.extend(true, $.magnificPopup.defaults, {
		iframe: {
			patterns: {
				youtube: {
					index: 'youtube.com/',
					id: 'v=',
					src: 'https://www.youtube.com/embed/%id%?autoplay=1'
				}
			}
		}
	});

	/*----------------------------
	START - Isotope
	------------------------------ */
	jQuery(".portfolio-item").isotope();
	$(".portfolio-menu li").on("click", function() {
		$(".portfolio-menu li").removeClass("active");
		$(this).addClass("active");
		var selector = $(this).attr('data-filter');
		$(".portfolio-item").isotope({
			filter: selector
		});
	});

	/*----------------------------
	START - Preloader
	------------------------------ */
	jQuery(window).load(function() {
		jQuery("#preloader").fadeOut(500);
	});

	/*----------------------------
	   START - Select Seats
	   ------------------------------ */
	document.addEventListener("DOMContentLoaded", function() {
		let selectedSeats = [];
		let totalPrice = 0;

		// Hàm xử lý chọn ghế
		function toggleSeatSelection(seatElement) {
			const seatId = seatElement.getAttribute("data-seat-id");
			const status = seatElement.getAttribute("data-status");

			if (status === "1") { // 1 = BOOKED
				alert("Ghế này đã được đặt!");
				return;
			}

			if (seatElement.classList.contains("selected")) {
				seatElement.classList.remove("selected");
				selectedSeats = selectedSeats.filter(id => id !== seatId);
			} else {
				seatElement.classList.add("selected");
				selectedSeats.push(seatId);
			}

			updateTicketInfo();
		}

		// Hàm cập nhật thông tin vé
		function updateTicketInfo() {
			const selectedSeatElements = document.querySelectorAll(".seat.selected");
			totalPrice = Array.from(selectedSeatElements).reduce((total, seat) => {
				const price = parseFloat(seat.getAttribute("data-price"));
				return total + price;
			}, 0);

			document.getElementById("selected-seat-count").innerText = selectedSeats.length;
			document.getElementById("total-price").innerText = totalPrice.toFixed(2) + "$";

			// Cập nhật số tiền trong form PayPal
			document.getElementById("paypal-amount").value = totalPrice.toFixed(2);
		}

		// Hàm xác nhận chọn ghế
		async function confirmSelection() {
			if (selectedSeats.length === 0) {
				alert("Vui lòng chọn ít nhất một ghế!");
				return;
			}

			const showtimeId = new URLSearchParams(window.location.search).get("showtimeId");
			if (!showtimeId) {
				alert("Không tìm thấy ID suất chiếu!");
				return;
			}

			confirmButton.disabled = true; // Vô hiệu hóa nút khi đang xử lý
			confirmButton.textContent = "Đang xử lý...";

			try {
				const response = await fetch('/movie/confirm-seats', {
					method: 'POST',
					headers: { 'Content-Type': 'application/json' },
					body: JSON.stringify({ seatIds: selectedSeats, showtimeId: parseInt(showtimeId) })
				});

				const data = await response.json();
				if (!response.ok) {
					if (response.status === 401) {
						window.location.href = "/account/login";
						return;
					}
					throw new Error(data.error || "Có lỗi xảy ra khi xác nhận ghế!");
				}

				window.location.href = data.redirectUrl || "/movie/booking-success";
			} catch (error) {
				alert(error.message);
			} finally {
				confirmButton.disabled = false; // Kích hoạt lại nút
				confirmButton.textContent = "Xác nhận";
			}
		}

		// Khởi tạo sự kiện cho các ghế
		document.querySelectorAll(".seat").forEach(seat => {
			seat.addEventListener("click", () => toggleSeatSelection(seat));
		});

		// Gắn sự kiện cho nút xác nhận
		const confirmButton = document.getElementById("confirm-selection");
		if (confirmButton) {
			confirmButton.addEventListener("click", confirmSelection);
		}
	});

	// Gắn sự kiện cho nút xác nhận
	document.querySelector("#confirm-selection")?.addEventListener("click", confirmSelection);

	document.addEventListener("DOMContentLoaded", function() {
		const form = document.getElementById("showtimeForm");
		const dateInput = document.getElementById("selectedDate");
		const brandInput = document.getElementById("selectedBrand");

		// Lấy ngày hiện tại nếu không có ngày được chọn
		function getCurrentDate() {
			const today = new Date();
			const year = today.getFullYear();
			const month = String(today.getMonth() + 1).padStart(2, "0");
			const day = String(today.getDate()).padStart(2, "0");
			return `${year}-${month}-${day}`;
		}

		// Xử lý sự kiện khi chọn ngày
		document.querySelectorAll("#scheduleHeader button").forEach(button => {
			button.addEventListener("click", function() {
				const selectedDate = this.getAttribute("data-date");
				dateInput.value = selectedDate;
				submitForm();
			});
		});

		// Xử lý sự kiện khi chọn thương hiệu rạp
		document.querySelectorAll(".cinema-tabs .tab-btn").forEach(button => {
			button.addEventListener("click", function() {
				const selectedBrand = this.getAttribute("data-name");
				brandInput.value = selectedBrand;

				// Loại bỏ class 'active' từ tất cả các tab
				document.querySelectorAll(".cinema-tabs .tab-btn").forEach(tab => {
					tab.classList.remove("active");
				});

				// Thêm class 'active' vào tab được chọn
				this.classList.add("active");

				submitForm();
			});
		});

		// Gửi form với dữ liệu đã chọn
		function submitForm() {
			if (!dateInput.value) {
				dateInput.value = getCurrentDate(); // Mặc định là ngày hôm nay
			}
			form.submit();
		}

		// Khởi tạo ngày mặc định khi trang tải
		dateInput.value = getCurrentDate();
	});

}(jQuery));