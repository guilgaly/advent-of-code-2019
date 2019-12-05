fn main() {
    let mut numbers: Vec<Vec<u8>> = Vec::new();
    for i1 in 0..10 {
        for i2 in i1..10 {
            for i3 in i2..10 {
                for i4 in i3..10 {
                    for i5 in i4..10 {
                        for i6 in i5..10 {
                            numbers.push(vec![i1, i2, i3, i4, i5, i6]);
                        }
                    }
                }
            }
        }
    }
    println!("1. size: {}", numbers.len());

    numbers = numbers.into_iter()
        .filter(|number| {
            let value: u32 = number.iter()
                .rev()
                .enumerate()
                .map(|(idx, d)| (*d as u32) * 10_u32.pow(idx as u32))
                .sum();
            124075 <= value && value <= 580769
        })
        .collect();
    println!("2. size: {}", numbers.len());

//    numbers = numbers.into_iter()
//        .filter(|number| {
//            let pairs = number[0..5].iter().zip(number[1..6].iter());
//            pairs.into_iter().find(|(x1, x2)| x1 == x2).is_some()
//        })
//        .collect();
//    println!("3. size: {}", numbers.len());

    numbers = numbers.into_iter()
        .filter(|number| {
            let mut i = 0;
            let mut curr_digit: Option<u8> = None;
            let mut curr_digit_count = 0;
            while i < 6 {
                if curr_digit == Some(number[i]) {
                    curr_digit_count += 1;
                } else if curr_digit_count == 2 {
                    break;
                } else {
                    curr_digit = Some(number[i]);
                    curr_digit_count = 1;
                }
                i += 1;
            }
            curr_digit_count == 2
        }).collect();
    println!("4. size: {}", numbers.len());
}
