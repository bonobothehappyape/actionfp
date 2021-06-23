import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IFramework, defaultValue } from 'app/shared/model/framework.model';

export const ACTION_TYPES = {
  SEARCH_FRAMEWORKS: 'framework/SEARCH_FRAMEWORKS',
  FETCH_FRAMEWORK_LIST: 'framework/FETCH_FRAMEWORK_LIST',
  FETCH_FRAMEWORK: 'framework/FETCH_FRAMEWORK',
  CREATE_FRAMEWORK: 'framework/CREATE_FRAMEWORK',
  UPDATE_FRAMEWORK: 'framework/UPDATE_FRAMEWORK',
  PARTIAL_UPDATE_FRAMEWORK: 'framework/PARTIAL_UPDATE_FRAMEWORK',
  DELETE_FRAMEWORK: 'framework/DELETE_FRAMEWORK',
  RESET: 'framework/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IFramework>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type FrameworkState = Readonly<typeof initialState>;

// Reducer

export default (state: FrameworkState = initialState, action): FrameworkState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_FRAMEWORKS):
    case REQUEST(ACTION_TYPES.FETCH_FRAMEWORK_LIST):
    case REQUEST(ACTION_TYPES.FETCH_FRAMEWORK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_FRAMEWORK):
    case REQUEST(ACTION_TYPES.UPDATE_FRAMEWORK):
    case REQUEST(ACTION_TYPES.DELETE_FRAMEWORK):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_FRAMEWORK):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_FRAMEWORKS):
    case FAILURE(ACTION_TYPES.FETCH_FRAMEWORK_LIST):
    case FAILURE(ACTION_TYPES.FETCH_FRAMEWORK):
    case FAILURE(ACTION_TYPES.CREATE_FRAMEWORK):
    case FAILURE(ACTION_TYPES.UPDATE_FRAMEWORK):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_FRAMEWORK):
    case FAILURE(ACTION_TYPES.DELETE_FRAMEWORK):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_FRAMEWORKS):
    case SUCCESS(ACTION_TYPES.FETCH_FRAMEWORK_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_FRAMEWORK):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_FRAMEWORK):
    case SUCCESS(ACTION_TYPES.UPDATE_FRAMEWORK):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_FRAMEWORK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_FRAMEWORK):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/frameworks';
const apiSearchUrl = 'api/_search/frameworks';

// Actions

export const getSearchEntities: ICrudSearchAction<IFramework> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_FRAMEWORKS,
  payload: axios.get<IFramework>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IFramework> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_FRAMEWORK_LIST,
  payload: axios.get<IFramework>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IFramework> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_FRAMEWORK,
    payload: axios.get<IFramework>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IFramework> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_FRAMEWORK,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IFramework> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_FRAMEWORK,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IFramework> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_FRAMEWORK,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IFramework> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_FRAMEWORK,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
