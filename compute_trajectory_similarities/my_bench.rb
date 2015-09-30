require 'benchmark'

n = 5000
a = (1..n).to_a.map do rand(100) end
Benchmark.bm do |x|
  x.report("product") {a.product(a) do |item| t = item.first + item.last end}
  x.report("combination(2)") {a.combination(2) do |item| t = item.first + item.last end}
  x.report("each each ") {a.each do |aa| a.each do |bb| t = aa + bb end end}
end
