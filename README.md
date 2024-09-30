# Национальный исследовательский университет "МЭИ"

## Лабораторная работа №2 по курсу «Моделирование»
Моделирование генераторов случайных чисел с заданным законом распределения вероятности.  
Выполнил студент группы А-04-22 Белоусов Егор Дмитриевич, вариант №1

## Задание
1.	Построить генератор случайных чисел с заданным законом распределения в соответствии с заданием. 
Написать и отладить программу, реализующую генератор на языке Паскаль или Си. 
Получить выборку неповторяющихся псевдослучайных чисел объемом не меньше 20 тыс.
2.	Определить период генератора случайных чисел. Если он меньше 10000, то изменить исходные данные.
3.	Провести анализ качества последовательности случайных чисел по критерию Пирсона.  

## Описание классов

### RandomGenerator
Основной класс для генерации последовательностей случайных чисел  
Генерация случайных чисел основана на методе середин квадратов для 64 разрядной сетки  
`List<Double> generateUniformDistributionSequence()` - генерирует последовательность чисел в промежутке [0, 1), 
распределенных по равномерному закону. Период - длина последовательности, за конец считается зацикиливание или обращение в 0  

`List<Double> generateSequenceWithDistribution(List<Double> uniformValues, Distribution distribution)` - 
генерирует последовательность чисел, распределенных по указанному закону

Может работать в автономном или ручном режиме

#### Автономный режим
Автоматически задает затравку с учетом текущего времени

#### Ручной режим
Затравку необходимо задавать вручную, если последовательность обратится в 0 или зациклиться не достигнув периода в 20_000 элементов,
то генератор попросит ввести другую затравку и так, пока не получится сгенерировать последовательность нужного периода

### Histogram
Класс для обработки полученной последовательности  
Для создания необходимо указать количество отрезков разбиения и закон распределения  

`List<Integer> getHist()` - возвращает список с количеством чисел попавших на каждый из интервалов  
`void addToHist(Double value)` - добавляет указанное значение в гистограмму  
`void addListToHist(List<Double> values)` - добавляет несколько значений в гистограмму  
`void clearHist()` - очищает гистограмму  
`int getHistSum()` - возвращает количество чисел в гистограмме  
`double getHistAvg()` - возвращает среднее значение чисел в гистограмме  
`int getSegmentsNumber()` - возвращает количество отрезков разбиения  
`List<Double> getSegments()` - возвращает координаты сегментов гистограммы  
`List<Integer> getProbabilityOfHittingTheSegment()` - возвращает вероятности попадания чисел на отрезок гистограммы 
согласно указанному распределению

### Distribution
Интерфейс, требующий реализации функции плотности распределения, функции распределения вероятностей, обратной функции 
распределения вероятностей, а также получение краев интервала 

### PythonExecutor
Выполняет python код.  
В данном случае код построения графиков и оценки по критерию Пирсона

### GeneratorRemote
Консольный интерфейс управления созданными классами

### evaluateAny.py
Оценивает произвольное распределение 

### evaluateUniform.py
Оценивает равномерное распределение