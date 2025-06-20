package com.library.mappers;

import com.library.dto.BookDTO;
import com.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookMapper MAPPER = Mappers.getMapper(BookMapper.class);
    @Mapping(target = "id", source = "id")
    BookDTO toDTO(Book book);
    @Mapping(target = "id", source = "id")
    Book toEntity(BookDTO bookDTO);
    List<BookDTO> toDTO(List<Book> bookList);
    List<Book> toEntity(List<BookDTO> bookDTOList);
    List<BookDTO> toDtoList(List<Book> books);
}