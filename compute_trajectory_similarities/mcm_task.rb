class McmTask < SimilarityTask

  # Constants defined in Motion Trajectory Segmentation via Min. Cost Multicuts.
  # Used in formula (7)
  BETA_0 = 2.0
  BETA_0_TILDE = 6.0
  BETA_1 = -0.02
  BETA_2 = -4.0
  BETA_3 = -0.02

  # alternative approach described in formula (7)
  def similarity(a, b)
    return EIGENSIM_VALUE if a == b
    # Find overlapping part of two given trajectories:
    #
    # find latest start frame of trajectory pair
    max_min_frame = [a,b].map(&:start_frame).max
    # find earliest end frame of trajectory pair
    min_max_frame = [a,b].map(&:end_frame).min
    # Compute affinities w(A,B)
    d_motions = motion_dist(a, b, max_min_frame, min_max_frame)
    d_motion = d_motions.empty? ? 0.0 : 255.0*d_motions.max
    d_spatial = avg_spatial_distance_between(a, b, max_min_frame, min_max_frame)
    d_color = color_dist(a, b, max_min_frame, min_max_frame)

    return 0.0 if min_max_frame-max_min_frame + 1 < 1

    z_ABs = [
      BETA_0_TILDE + BETA_1*d_motion + BETA_2*d_spatial + BETA_3*d_color,
      BETA_0 + BETA_1*d_motion
    ]
    z_AB = z_ABs.max
    1.0 / (1.0 + Math.exp(-z_AB))
  end

  def color_dist(a, b, lower_idx, upper_idx)
    len = 0.0
    (lower_idx..upper_idx).each do |idx|
      pa = a.point_at(idx)
      pb = b.point_at(idx)
      lab_a = CieLab.bilinear_interpolated_color_for(pa, idx)
      lab_b = CieLab.bilinear_interpolated_color_for(pb, idx)
      len = len + lab_a.copy.sub(lab_b).length
    end
    len = len / (upper_idx-lower_idx+1)
  end

  def motion_dist(a,b,lower_idx, upper_idx, dt=1)
    common_frame_count = upper_idx-lower_idx+1
    return [] if common_frame_count < 2

    # if common frame count is 2, then use a stepsize equal to dt=1
    timestep = $is_debugging ? dt : ([common_frame_count, DT_THREH+1].min - 1)

    # ensure that we only iterate over trajectories that are longer that 4 segments
    u = upper_idx-timestep #[upper_idx-timestep, upper_idx - DT_THREH].max
    l = lower_idx
    return [] if u < l # when forward diff cannot be computed

    # compute average spatial distance between 2 trajectories
    # over all overalapping segments.
    d_sp_a_b = avg_spatial_distance_between(a, b, l, u)

    # append spacial dist to traj
    append_avg_spatial_distances(a, b, d_sp_a_b)

    (l..u).map do |idx|
      # compute foreward diff over T for A,B
      dt_A = foreward_differece_on(a, timestep, idx)
      dt_B = foreward_differece_on(b, timestep, idx)
      dt_AB = dt_A.sub(dt_B).length_squared
      sigma_t = use_local_variance? ? local_sigma_t_at(idx, a, b) : sigma_t_at(idx)
      s_dt = (USE_WINDOWING_VAR) ? timestep : 1
      sigma_t = EPS_FLOW+local_sigma_t_at(idx, a, b, s_dt)
      Math.sqrt(dt_AB/sigma_t)
    end

  end

end
