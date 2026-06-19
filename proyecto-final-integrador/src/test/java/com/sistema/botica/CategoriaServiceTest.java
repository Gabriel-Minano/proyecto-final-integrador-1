package com.sistema.botica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sistema.botica.Repository.CategoriaRepository;
import com.sistema.botica.entity.Categoria;
import com.sistema.botica.service.CategoriaService;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    public void buscarPorIdMockito() {
        Categoria categoria = new Categoria(1, "Jarabes", true, null);
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarPorId(1);

        assertNotNull(resultado);
        assertEquals("Jarabes", resultado.getNombre());

        verify(categoriaRepository, times(1)).findById(1);
    }

    @Test
    public void crearCategoriaMockito() {
        Categoria categoria = new Categoria(null, "Pastillas", true, null);
        Categoria categoriaResult = new Categoria(14, "Pastillas", true, null);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaResult);

        Categoria resultado = categoriaService.guardar2(categoria);

        assertNotNull(resultado);
        assertEquals(14, resultado.getIdCategoria());
        assertEquals("Pastillas",resultado.getNombre());

        verify(categoriaRepository, times(1)).save(categoria);
    }
}