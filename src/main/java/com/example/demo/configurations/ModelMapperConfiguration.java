package com.example.demo.configurations;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.example.demo.dto.ActorDTO;
import com.example.demo.dto.BookingDetailsDTO;
import com.example.demo.dto.BookingHistoryDTO;
import com.example.demo.dto.BookingsDTO;
import com.example.demo.dto.BrandDTO;
import com.example.demo.dto.DirectorDTO;
import com.example.demo.dto.GenresDTO;
import com.example.demo.dto.MoviesDTO;
import com.example.demo.dto.ScreensDTO;
import com.example.demo.dto.SeatsDTO;
import com.example.demo.dto.ShowtimesDTO;
import com.example.demo.dto.TheatersDTO;
import com.example.demo.dto.UsersDTO;
import com.example.demo.entities.Actor;
import com.example.demo.entities.BookingDetails;
import com.example.demo.entities.Bookings;
import com.example.demo.entities.Brands;
import com.example.demo.entities.Director;
import com.example.demo.entities.Genres;
import com.example.demo.entities.Movies;
import com.example.demo.entities.Roles;
import com.example.demo.entities.Screens;
import com.example.demo.entities.Seats;
import com.example.demo.entities.Showtimes;
import com.example.demo.entities.Theaters;
import com.example.demo.entities.Users;

@Configuration
public class ModelMapperConfiguration {

	@Autowired
	private Environment environment;

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

		/* Users vs UsersDTO */
		mapper.addMappings(new PropertyMap<Users, UsersDTO>() {
			@Override
			protected void configure() {
				map().setUserId(source.getUserId());
				map().setEmail(source.getEmail());
				map().setFullName(source.getFullName());
				map().setPassword(source.getPassword());
				map().setDob(source.getDob());
				map().setSecurityCode(source.getSecurityCode());
			}
		});

		Converter<Roles, Integer> converterRoleToRoleId = new AbstractConverter<Roles, Integer>() {
			@Override
			protected Integer convert(Roles roles) {
				return roles != null ? roles.getRoleId() : null;
			}
		};

		Converter<Roles, String> converterRoleToRoleName = new AbstractConverter<Roles, String>() {
			@Override
			protected String convert(Roles roles) {
				return roles != null ? roles.getName() : null;
			}
		};

		Converter<Integer, Roles> converterRoleIdToRole = new AbstractConverter<Integer, Roles>() {
			@Override
			protected Roles convert(Integer roleId) {
				if (roleId == null)
					return null;
				Roles role = new Roles();
				role.setRoleId(roleId);
				return role;
			}
		};

		mapper.addMappings(new PropertyMap<UsersDTO, Users>() {
			@Override
			protected void configure() {
				map().setUserId(source.getUserId());
				map().setEmail(source.getEmail());
				map().setFullName(source.getFullName());
				map().setPassword(source.getPassword());
				map().setDob(source.getDob());
				map().setSecurityCode(source.getSecurityCode());
			}
		});

		mapper.typeMap(Users.class, UsersDTO.class).addMappings(map -> {
			map.using(converterRoleToRoleId).map(Users::getRoles, UsersDTO::setRoleId);
			map.using(converterRoleToRoleName).map(Users::getRoles, UsersDTO::setRoleName);
		});

		mapper.typeMap(UsersDTO.class, Users.class).addMappings(map -> {
			map.using(converterRoleIdToRole).map(UsersDTO::getRoleId, Users::setRoles);
		});

		/* Movies vs MoviesDTO */
		mapper.addMappings(new PropertyMap<Movies, MoviesDTO>() {
			@Override
			protected void configure() {
				map().setMovieId(source.getMovieId());
				map().setTitle(source.getTitle());
				map().setDescription(source.getDescription());
				map().setDuration(source.getDuration());
				map().setReleaseDate(source.getReleaseDate());
				map().setImageUrl(source.getImageUrl());
				map().setTrailerUrl(source.getTrailerUrl());
				map().setStatus(source.getStatus());
				map().setRating(source.getRating());
			}
		});

		// Converter cho Actor -> ActorDTO
		Converter<List<Actor>, List<ActorDTO>> actorConverter = ctx -> ctx.getSource() == null ? List.of()
				: ctx.getSource().stream().map(actor -> {
					ActorDTO dto = new ActorDTO();
					dto.setId(actor.getId());
					dto.setName(actor.getName());
					dto.setImageUrl(
							actor.getImageUrl() != null ? environment.getProperty("images_url") + actor.getImageUrl()
									: null);
					return dto;
				}).collect(Collectors.toList());

		// Converter cho Director -> DirectorDTO với đường dẫn
		Converter<List<Director>, List<DirectorDTO>> directorConverter = ctx -> ctx.getSource() == null ? List.of()
				: ctx.getSource().stream().map(director -> {
					DirectorDTO dto = new DirectorDTO();
					dto.setId(director.getId());
					dto.setName(director.getName());
					dto.setImageUrl(director.getImageUrl() != null
							? environment.getProperty("images_url") + director.getImageUrl()
							: null);
					return dto;
				}).collect(Collectors.toList());

//		Converter<List<Actor>, List<String>> actorConverter = ctx -> ctx.getSource() == null ? List.of()
//				: ctx.getSource().stream().map(Actor::getName).collect(Collectors.toList());
//
//		Converter<List<Director>, List<String>> directorConverter = ctx -> ctx.getSource() == null ? List.of()
//				: ctx.getSource().stream().map(Director::getName).collect(Collectors.toList());

		// Sửa genresConverter để xử lý Collection<Genres> thay vì Set<Genres>
		Converter<Collection<Genres>, List<GenresDTO>> genresConverter = ctx -> ctx.getSource() == null ? List.of()
				: ctx.getSource().stream().map(genre -> mapper.map(genre, GenresDTO.class))
						.collect(Collectors.toList());

		Converter<List<Theaters>, List<String>> theatersConverter = ctx -> ctx.getSource() == null ? List.of()
				: ctx.getSource().stream().map(Theaters::getName).collect(Collectors.toList());

		Converter<String, String> converterPhotoToPhotoUrl = new AbstractConverter<String, String>() {
			@Override
			protected String convert(String photo) {
				return photo != null ? environment.getProperty("images_url") + photo : null;
			}
		};

		Converter<String, String> converterTrailerToTrailer = new AbstractConverter<String, String>() {
			@Override
			protected String convert(String trailer) {
				return trailer != null ? environment.getProperty("trailer") + trailer : null;
			}
		};

		mapper.typeMap(Movies.class, MoviesDTO.class).addMappings(map -> {
			map.using(actorConverter).map(Movies::getActors, MoviesDTO::setActors);
			map.using(directorConverter).map(Movies::getDirectors, MoviesDTO::setDirectors);
			map.using(genresConverter).map(Movies::getGenreses, MoviesDTO::setGenres);
			map.using(theatersConverter).map(Movies::getTheaterses, MoviesDTO::setTheaters);
			map.using(converterPhotoToPhotoUrl).map(Movies::getImageUrl, MoviesDTO::setPhotoUrl);
			map.using(converterTrailerToTrailer).map(Movies::getTrailerUrl, MoviesDTO::setTrailer);
		});

		mapper.addMappings(new PropertyMap<MoviesDTO, Movies>() {
			@Override
			protected void configure() {
				map().setMovieId(source.getMovieId());
				map().setTitle(source.getTitle());
				map().setDescription(source.getDescription());
				map().setDuration(source.getDuration());
				map().setReleaseDate(source.getReleaseDate());
				map().setImageUrl(source.getImageUrl());
				map().setTrailerUrl(source.getTrailerUrl());
				map().setStatus(source.getStatus());
				map().setRating(source.getRating());
			}
		});

		/* Genres vs GenresDTO */
		mapper.addMappings(new PropertyMap<Genres, GenresDTO>() {
			@Override
			protected void configure() {
				map().setGenreId(source.getGenreId());
				map().setName(source.getName());
			}
		});

		/* Theater vs TheatersDTO */
		mapper.addMappings(new PropertyMap<Theaters, TheatersDTO>() {
			@Override
			protected void configure() {
				map().setTheaterId(source.getTheaterId());
				map().setName(source.getName());
				map().setAddress(source.getAddress());
				map().setBrandId(source.getBrands() != null ? source.getBrands().getBrandId() : null);
			}
		});

		Converter<Theaters, String> brandImageConverter = new AbstractConverter<Theaters, String>() {
			@Override
			protected String convert(Theaters theaters) {
				if (theaters != null && theaters.getBrands() != null && theaters.getBrands().getImageUrl() != null) {
					String baseUrl = environment.getProperty("brands_url");
					return baseUrl + theaters.getBrands().getImageUrl();
				}
				return null;
			}
		};

		Converter<List<Screens>, List<ScreensDTO>> screensConverter = ctx -> ctx.getSource() == null ? List.of()
				: ctx.getSource().stream().map(screen -> mapper.map(screen, ScreensDTO.class))
						.collect(Collectors.toList());

		mapper.typeMap(Theaters.class, TheatersDTO.class).addMappings(map -> {
			map.using(brandImageConverter).map(Theaters::getBrands, TheatersDTO::setBrandImage);
			map.using(screensConverter).map(Theaters::getScreenses, TheatersDTO::setScreens);
		});

		/* ShowTimes vs ShowTimesDTO */
		mapper.addMappings(new PropertyMap<Showtimes, ShowtimesDTO>() {
			@Override
			protected void configure() {
				map().setShowtimeId(source.getShowtimeId());
				map().setShowDate(source.getShowDate());
				map().setStartTime(source.getStartTime());
				map().setEndTime(source.getEndTime());
			}
		});

		/* Screens vs ScreensDTO */
		mapper.addMappings(new PropertyMap<Screens, ScreensDTO>() {
			@Override
			protected void configure() {
				map().setScreenId(source.getScreenId());
				map().setName(source.getName());
				map().setTotalSeats(source.getTotalSeats());
			}
		});

		Converter<Theaters, Integer> converterTheaterToTheaterId = new AbstractConverter<Theaters, Integer>() {
			@Override
			protected Integer convert(Theaters theaters) {
				return theaters != null ? theaters.getTheaterId() : null;
			}
		};

		mapper.typeMap(Screens.class, ScreensDTO.class).addMappings(map -> {
			map.using(converterTheaterToTheaterId).map(Screens::getTheaters, ScreensDTO::setTheaterId);
		});

		/* Seats vs SeatsDTO */
		mapper.addMappings(new PropertyMap<Seats, SeatsDTO>() {
			@Override
			protected void configure() {
				map().setSeatId(source.getSeatId());
				map().setSeatNumber(source.getSeatNumber());
				map().setPrice(source.getPrice());
				map().setStatus(source.getStatus());
			}
		});

		Converter<Screens, Integer> screenToIdConverter = new AbstractConverter<Screens, Integer>() {
			@Override
			protected Integer convert(Screens screens) {
				return screens != null ? screens.getScreenId() : null;
			}
		};

		mapper.typeMap(Seats.class, SeatsDTO.class).addMappings(map -> {
			map.using(screenToIdConverter).map(Seats::getScreens, SeatsDTO::setScreensId);
		});

		/* Định dạng ngày giờ */
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

		/* Bookings vs BookingsDTO */
		mapper.addMappings(new PropertyMap<Bookings, BookingsDTO>() {
			@Override
			protected void configure() {
				map().setBookingId(source.getBookingId());
				map().setBookingDate(source.getBookingDate());
				map().setTotalPrice(source.getTotalPrice());
				map().setBarcode(source.getBarcode());
			}
		});

		Converter<Users, Integer> userToUserIdConverter = new AbstractConverter<Users, Integer>() {
			@Override
			protected Integer convert(Users user) {
				return user != null ? user.getUserId() : null;
			}
		};

		Converter<Set<BookingDetails>, List<BookingDetailsDTO>> bookingDetailsConverter = ctx -> ctx.getSource() == null
				? List.of()
				: ctx.getSource().stream().map(detail -> mapper.map(detail, BookingDetailsDTO.class))
						.collect(Collectors.toList());

		mapper.typeMap(Bookings.class, BookingsDTO.class).addMappings(map -> {
			map.using(userToUserIdConverter).map(Bookings::getUsers, BookingsDTO::setUserId);
			map.using(bookingDetailsConverter).map(Bookings::getBookingDetailses, BookingsDTO::setBookingDetails);
		});

		/* BookingDetails vs BookingDetailsDTO */
		mapper.addMappings(new PropertyMap<BookingDetails, BookingDetailsDTO>() {
			@Override
			protected void configure() {
				map().setBookingDetailId(source.getBookingDetailId());
				map().setPrice(source.getPrice());
			}
		});

		Converter<Seats, Integer> seatToSeatIdConverter = new AbstractConverter<Seats, Integer>() {
			@Override
			protected Integer convert(Seats seat) {
				return seat != null ? seat.getSeatId() : null;
			}
		};

		Converter<Showtimes, Integer> showtimeToShowtimeIdConverter = new AbstractConverter<Showtimes, Integer>() {
			@Override
			protected Integer convert(Showtimes showtime) {
				return showtime != null ? showtime.getShowtimeId() : null;
			}
		};

		mapper.typeMap(BookingDetails.class, BookingDetailsDTO.class).addMappings(map -> {
			map.using(seatToSeatIdConverter).map(BookingDetails::getSeats, BookingDetailsDTO::setSeatId);
			map.using(showtimeToShowtimeIdConverter).map(BookingDetails::getShowtimes,
					BookingDetailsDTO::setShowtimeId);
		});

		/* Bookings vs BookingHistoryDTO */
		mapper.addMappings(new PropertyMap<Bookings, BookingHistoryDTO>() {
			@Override
			protected void configure() {
				map().setBookingId(source.getBookingId());
				map().setBookingDate(source.getBookingDate());
				map().setTotalPrice(source.getTotalPrice());
				map().setBarcode(source.getBarcode());
			}
		});

		Converter<Set<BookingDetails>, List<BookingHistoryDTO.BookingDetailDTO>> bookingDetailsToHistoryConverter = ctx -> ctx
				.getSource() == null ? List.of() : ctx.getSource().stream().map(detail -> {
					BookingHistoryDTO.BookingDetailDTO dto = new BookingHistoryDTO.BookingDetailDTO();
					dto.setBookingDetailId(detail.getBookingDetailId());
					dto.setSeatNumber(detail.getSeats().getSeatNumber());
					dto.setPrice(detail.getPrice());
					dto.setTheaterName(detail.getShowtimes().getMovieShowtimeScreens().stream().findFirst()
							.map(mss -> mss.getScreens().getTheaters().getName()).orElse("Unknown"));
					dto.setShowDate(dateFormat.format(detail.getShowtimes().getShowDate()));
					dto.setStartTime(timeFormat.format(detail.getShowtimes().getStartTime()));
					dto.setMovieTitle(detail.getShowtimes().getMovieShowtimeScreens().stream().findFirst()
							.map(mss -> mss.getMovies().getTitle()).orElse("Unknown"));
					return dto;
				}).collect(Collectors.toList());

		mapper.typeMap(Bookings.class, BookingHistoryDTO.class).addMappings(map -> {
			map.using(bookingDetailsToHistoryConverter).map(Bookings::getBookingDetailses,
					BookingHistoryDTO::setBookingDetails);
		});

		/* Brands vs BrandDTO */
		mapper.addMappings(new PropertyMap<Brands, BrandDTO>() {
			@Override
			protected void configure() {
				map().setBrandId(source.getBrandId());
				map().setBrandName(source.getName());
//				map().setBrandImage(source.getImageUrl());
			}
		});

		mapper.typeMap(Brands.class, BrandDTO.class).addMappings(map -> {
			map.using(converterPhotoToPhotoUrl).map(Brands::getImageUrl, BrandDTO::setBrandImage);
		});

		/* Actor vs ActorDTO */
		mapper.addMappings(new PropertyMap<Actor, ActorDTO>() {
			@Override
			protected void configure() {
				map().setId(source.getId());
				map().setName(source.getName());

			}
		});

		Converter<String, String> converterImagesToPhotoUrl = new AbstractConverter<String, String>() {
			@Override
			protected String convert(String photo) {
				return photo != null ? "http://172.16.14.43:8088/assets/images/" + photo : null;
			}
		};

		mapper.typeMap(Actor.class, ActorDTO.class).addMappings(map -> {
			map.using(converterImagesToPhotoUrl).map(Actor::getImageUrl, ActorDTO::setImageUrl);
		});

		/* Director vs DirectorDTO */
		mapper.addMappings(new PropertyMap<Director, DirectorDTO>() {
			@Override
			protected void configure() {
				map().setId(source.getId());
				map().setName(source.getName());

			}
		});

		mapper.typeMap(Director.class, DirectorDTO.class).addMappings(map -> {
			map.using(converterPhotoToPhotoUrl).map(Director::getImageUrl, DirectorDTO::setImageUrl);
		});

		return mapper;
	}
}