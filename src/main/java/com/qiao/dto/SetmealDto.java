package com.qiao.dto;

import com.qiao.entity.Setmeal;
import com.qiao.entity.SetmealDish;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {
    private String categoryName;

    private List<SetmealDish>  setmealDishes = new ArrayList<>();

    private Integer copies;





}
