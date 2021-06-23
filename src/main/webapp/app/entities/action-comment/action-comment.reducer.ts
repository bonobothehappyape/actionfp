import axios from 'axios';
import { ICrudSearchAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IActionComment, defaultValue } from 'app/shared/model/action-comment.model';

export const ACTION_TYPES = {
  SEARCH_ACTIONCOMMENTS: 'actionComment/SEARCH_ACTIONCOMMENTS',
  FETCH_ACTIONCOMMENT_LIST: 'actionComment/FETCH_ACTIONCOMMENT_LIST',
  FETCH_ACTIONCOMMENT: 'actionComment/FETCH_ACTIONCOMMENT',
  CREATE_ACTIONCOMMENT: 'actionComment/CREATE_ACTIONCOMMENT',
  UPDATE_ACTIONCOMMENT: 'actionComment/UPDATE_ACTIONCOMMENT',
  PARTIAL_UPDATE_ACTIONCOMMENT: 'actionComment/PARTIAL_UPDATE_ACTIONCOMMENT',
  DELETE_ACTIONCOMMENT: 'actionComment/DELETE_ACTIONCOMMENT',
  RESET: 'actionComment/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IActionComment>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type ActionCommentState = Readonly<typeof initialState>;

// Reducer

export default (state: ActionCommentState = initialState, action): ActionCommentState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_ACTIONCOMMENTS):
    case REQUEST(ACTION_TYPES.FETCH_ACTIONCOMMENT_LIST):
    case REQUEST(ACTION_TYPES.FETCH_ACTIONCOMMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_ACTIONCOMMENT):
    case REQUEST(ACTION_TYPES.UPDATE_ACTIONCOMMENT):
    case REQUEST(ACTION_TYPES.DELETE_ACTIONCOMMENT):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_ACTIONCOMMENT):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.SEARCH_ACTIONCOMMENTS):
    case FAILURE(ACTION_TYPES.FETCH_ACTIONCOMMENT_LIST):
    case FAILURE(ACTION_TYPES.FETCH_ACTIONCOMMENT):
    case FAILURE(ACTION_TYPES.CREATE_ACTIONCOMMENT):
    case FAILURE(ACTION_TYPES.UPDATE_ACTIONCOMMENT):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_ACTIONCOMMENT):
    case FAILURE(ACTION_TYPES.DELETE_ACTIONCOMMENT):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.SEARCH_ACTIONCOMMENTS):
    case SUCCESS(ACTION_TYPES.FETCH_ACTIONCOMMENT_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_ACTIONCOMMENT):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_ACTIONCOMMENT):
    case SUCCESS(ACTION_TYPES.UPDATE_ACTIONCOMMENT):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_ACTIONCOMMENT):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_ACTIONCOMMENT):
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

const apiUrl = 'api/action-comments';
const apiSearchUrl = 'api/_search/action-comments';

// Actions

export const getSearchEntities: ICrudSearchAction<IActionComment> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_ACTIONCOMMENTS,
  payload: axios.get<IActionComment>(`${apiSearchUrl}?query=${query}`),
});

export const getEntities: ICrudGetAllAction<IActionComment> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_ACTIONCOMMENT_LIST,
  payload: axios.get<IActionComment>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IActionComment> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_ACTIONCOMMENT,
    payload: axios.get<IActionComment>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IActionComment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_ACTIONCOMMENT,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IActionComment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_ACTIONCOMMENT,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IActionComment> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_ACTIONCOMMENT,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IActionComment> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_ACTIONCOMMENT,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
