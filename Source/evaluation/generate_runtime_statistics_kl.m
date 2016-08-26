INPUT_FILE_PATH = '../output/runtimes/';
timestamp = '1471957394';
target_file = strcat('core_measurments_', timestamp, '.txt');


timestamp = '1472253172';
target_file = strcat('kl_measurments_', timestamp, '.txt');

APPLY_QUANTILE_FILTERING = false;

fpname = strcat(INPUT_FILE_PATH, target_file);
% file format: data is sorted according to first column
% => 1st column: trajectory count
% => 2nd column: runtime
statistics = textread(fpname);
trajectory_counts = statistics(:, 1);
timings = statistics(:, 2);


% start aggregate data
different_counts = unique(trajectory_counts);
tmp_tc = zeros(1, length(different_counts));
tmp_dt = zeros(1, length(different_counts));
for k=1:length(different_counts)
    idxs = find( trajectory_counts == different_counts(k));
    tmp_dt(k) = sum(timings(idxs))/length(idxs);
    tmp_tc(k) = different_counts(k);
end
trajectory_counts = tmp_tc';
timings = tmp_dt';

% end


has_enough_trajectories = trajectory_counts > 100;
trajectory_counts = trajectory_counts(has_enough_trajectories);
timings = timings(has_enough_trajectories);

if APPLY_QUANTILE_FILTERING
    qs = quantile(timings, 10);
    valids = timings <= qs(9);
    timings = timings(valids);
    trajectory_counts = trajectory_counts(valids);
end
%plot(trajectory_counts, timings, '.')

quad_fit = fit(trajectory_counts, timings,'poly2');
h1 = plot(quad_fit, trajectory_counts, timings);
legend(h1,'off')

ylabel('#Trajectories x #Iterations', 'fontsize', 20)
xlabel('Time [s]', 'fontsize', 20)


%lin_fit = fit(trajectory_counts,timings,'poly1');
%plot(lin_fit, trajectory_counts, timings);