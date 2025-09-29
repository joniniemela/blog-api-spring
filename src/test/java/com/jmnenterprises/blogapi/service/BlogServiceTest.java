package com.jmnenterprises.blogapi.service;

import com.jmnenterprises.blogapi.repository.BlogRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;


@ExtendWith(MockitoExtension.class)
public class BlogServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    BlogServiceImpl blogService;


}
